package org.finos.springbot.symphony.messages;

import java.util.HashMap;

import org.finos.springbot.symphony.content.SymphonyUser;
import org.finos.springbot.symphony.content.serialization.MessageMLParser;
import org.finos.springbot.symphony.json.EntityJsonConverter;
import org.finos.springbot.workflow.annotations.ChatVariable;
import org.finos.springbot.workflow.content.Content;
import org.finos.springbot.workflow.content.Message;
import org.finos.springbot.workflow.content.OrderedList;
import org.finos.springbot.workflow.content.Paragraph;
import org.finos.springbot.workflow.content.Table;
import org.finos.springbot.workflow.content.UnorderedList;
import org.finos.springbot.workflow.content.Word;
import org.finos.springbot.workflow.java.mapping.MessageMatcher;
import org.finos.symphony.toolkit.json.EntityJson;
import org.finos.symphony.toolkit.workflow.AbstractMockSymphonyTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class TestMessageMLParser extends AbstractMockSymphonyTest {

	MessageMLParser smp = new MessageMLParser();
	
	@Autowired
	EntityJsonConverter entityJsonConverter;
		
	@Test
	public void testMessageWithTable() throws Exception {
		Content c = smp.apply(
				"<div data-format=\"PresentationML\" data-version=\"2.0\" class=\"wysiwyg\"><p><table class=\"pasted-table\"><thead><tr><th>Name</th><th>Age</th><th>Alive</th></tr></thead><tbody><tr><td>Jim</td><td>5</td><td>FALSE</td></tr><tr><td>James</td><td>7</td><td>TRUE</td></tr></tbody></table></p></div>",
				new EntityJson());
		
		Assertions.assertEquals(Paragraph.of("Age"), c.getNth(Table.class, 0).get().getColumnNames().get(1));

		Assertions.assertEquals(Paragraph.of("TRUE"), c.getNth(Table.class, 0).get().getData().get(1).get(2));

	}
	
	@Test
	public void testMessageMatcherExact() throws Exception {
		Content c = smp.apply("hello some words");
		MessageMatcher m1 = new MessageMatcher(c);
		Assertions.assertTrue(m1.consume(c, new HashMap<ChatVariable, Object>()));
	}
	
	@Test
	public void testMessageMatcherMore() throws Exception {
		Content pattern = smp.apply("hello some words");
		Content c2 = smp.apply("hello some words and some more words");
		MessageMatcher m1 = new MessageMatcher(pattern);
		Assertions.assertTrue(m1.consume(c2, new HashMap<ChatVariable, Object>()));
		
		Content c3 = smp.apply("hello some different words");
		Assertions.assertFalse(m1.consume(c3, new HashMap<ChatVariable, Object>()));
	}
	
	@Test
	public void testRemoveSlash() throws Exception {
		Content pattern = smp.apply("/hello some words");
		Content c2 = smp.apply("bob some words");
		Assertions.assertEquals(c2, pattern.replace(Word.of("/hello"), Word.of("bob")));
	}

	@Test
	public void testSimpleMessage() throws Exception {
		Assertions.assertEquals(
			Message.of("this is it"),
			smp.apply("<messageML><p>this is it</p></messageML>", null));
	}
	
	@Test
	public void testUnorderedListMessage() throws Exception {
		Assertions.assertEquals(
			Message.of(UnorderedList.of(Paragraph.of("First"), Paragraph.of("Second"))),
			smp.apply("<messageML><ul><li>First</li><li>Second</li></ul></messageML>", null));
	}
	
	@Test
	public void testOrderedListMessage() throws Exception {
		Assertions.assertEquals(
			Message.of(OrderedList.of(Paragraph.of("First"), Paragraph.of("Second"))),
			smp.apply("<messageML><ol><li>First</li><li>Second</li></ol></messageML>", null));
	}
	
	@Test
	public void testHelpMessage() throws Exception {
		Assertions.assertEquals(
			Message.of(Word.of("help")),
			smp.apply("<messageML>Help</messageML>", null));
	}
	
	@Test
	public void testTaggedMessage() throws Exception {
		EntityJson ej = entityJsonConverter.readValue("{\"0\":{\"id\":[{\"type\":\"com.symphony.user.userId\",\"value\":\"347583113331315\"}],\"type\":\"com.symphony.user.mention\"},\"1\":{\"id\":[{\"type\":\"com.symphony.user.userId\",\"value\":\"345315370604167\"}],\"type\":\"com.symphony.user.mention\"},\"2\":{\"id\":[{\"type\":\"com.symphony.user.userId\",\"value\":\"345315370598706\"}],\"type\":\"com.symphony.user.mention\"}}");
		Message actual = smp.apply(
			"<div data-format=\"PresentationML\" data-version=\"2.0\" class=\"wysiwyg\"><p> </p><p>/help <span class=\"entity\" data-entity-id=\"0\">@Rob Moffat</span> <span class=\"entity\" data-entity-id=\"1\">@Mark Mainwood</span> <span class=\"entity\" data-entity-id=\"2\">@James Tan</span> </p></div>",
			ej);
		Message expected = Message.of(
				Paragraph.of(),
				Paragraph.of(
						Word.of("/help"), 
						new SymphonyUser(347583113331315l, "Rob Moffat",null),
						new SymphonyUser(345315370604167l, "Mark Mainwood",null),
						new SymphonyUser(345315370598706l, "James Tan", null)));
		Assertions.assertEquals(
			expected, 
			actual);
	}
	

}
