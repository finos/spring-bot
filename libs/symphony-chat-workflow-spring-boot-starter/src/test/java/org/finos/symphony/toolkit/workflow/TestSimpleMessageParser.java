package org.finos.symphony.toolkit.workflow;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.stream.Collectors;

import org.finos.symphony.toolkit.json.EntityJson;
import org.finos.symphony.toolkit.workflow.annotations.ChatVariable;
import org.finos.symphony.toolkit.workflow.content.Content;
import org.finos.symphony.toolkit.workflow.content.Message;
import org.finos.symphony.toolkit.workflow.content.OrderedList;
import org.finos.symphony.toolkit.workflow.content.Paragraph;
import org.finos.symphony.toolkit.workflow.content.PastedTable;
import org.finos.symphony.toolkit.workflow.content.UnorderedList;
import org.finos.symphony.toolkit.workflow.content.Word;
import org.finos.symphony.toolkit.workflow.java.mapping.MessageMatcher;
import org.finos.symphony.toolkit.workflow.sources.symphony.content.UserDef;
import org.finos.symphony.toolkit.workflow.sources.symphony.json.EntityJsonConverter;
import org.finos.symphony.toolkit.workflow.sources.symphony.messages.SimpleMessageParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;

public class TestSimpleMessageParser extends AbstractMockSymphonyTest {

	SimpleMessageParser smp = new SimpleMessageParser();
	
	@Autowired
	EntityJsonConverter entityJsonConverter;
		
	@Test
	public void testMessageWithTable() throws Exception {
		Content c = smp.parse(
				"<div data-format=\"PresentationML\" data-version=\"2.0\" class=\"wysiwyg\"><p><table class=\"pasted-table\"><thead><tr><th>Name</th><th>Age</th><th>Alive</th></tr></thead><tbody><tr><td>Jim</td><td>5</td><td>FALSE</td></tr><tr><td>James</td><td>7</td><td>TRUE</td></tr></tbody></table></p></div>",
				new EntityJson());
		
		Assertions.assertEquals(Paragraph.of(Arrays.asList(Word.of("Age"))), c.getNth(PastedTable.class, 0).get().getColumnNames().get(1));

		Assertions.assertEquals(Paragraph.of(Arrays.asList(Word.of("TRUE"))), c.getNth(PastedTable.class, 0).get().getData().get(1).get(2));

	}
	
	@Test
	public void testWithout() {
		Word one = Word.of("one");
		Word two = Word.of("two");
		Word three = Word.of("three");
		Paragraph p1 = Paragraph.of(Arrays.asList(one, two, three));
		Paragraph p2 = Paragraph.of(Arrays.asList(one, three));
		Paragraph p1_ = Paragraph.of(Arrays.asList(one, two));
		Paragraph p2_ = Paragraph.of(Arrays.asList(one));
		Message m1 = Message.of(Arrays.asList(p1, p2));
		Message m2 = Message.of(Arrays.asList(p1_, p2_));
		
		
		Assertions.assertEquals(
				m1.without(three),
				m2);
	}
	
	@Test
	public void testRemoveStart() {
		Word one = Word.of("one");
		Word two = Word.of("two");
		Word three = Word.of("three");
		Paragraph p1 = Paragraph.of(Arrays.asList(one, two, three));
		Paragraph p1_ = Paragraph.of(Arrays.asList(two, three));
		Message m1 = Message.of(Arrays.asList(p1));
		Message m2 = Message.of(Arrays.asList(p1_));
		
		
		Assertions.assertEquals(
				m1.removeAtStart(one),
				m2);
	}
	
	@Test
	public void testMessageMatcherExact() throws Exception {
		Content c = smp.parseNaked("hello some words");
		MessageMatcher m1 = new MessageMatcher(c);
		Assertions.assertTrue(m1.consume(c, new HashMap<ChatVariable, Content>()));
	}
	
	@Test
	public void testMessageMatcherMore() throws Exception {
		Content pattern = smp.parseNaked("hello some words");
		Content c2 = smp.parseNaked("hello some words and some more words");
		MessageMatcher m1 = new MessageMatcher(pattern);
		Assertions.assertTrue(m1.consume(c2, new HashMap<ChatVariable, Content>()));
		
		Content c3 = smp.parseNaked("hello some different words");
		Assertions.assertFalse(m1.consume(c3, new HashMap<ChatVariable, Content>()));
	}

	@Test
	public void testSimpleMessage() throws Exception {
		Assertions.assertEquals(
			Message.of(
				Arrays.asList(
					Paragraph.of(
						Arrays.stream(new String[] {"this", "is", "it"})
							.map(s -> Word.of(s))
							.collect(Collectors.toList())
						))), smp.parse("<messageML><p>this is it</p></messageML>", null));
	}
	
	@Test
	public void testUnorderedListMessage() throws Exception {
		Assertions.assertEquals(
			Message.of(
				Arrays.asList(
					UnorderedList.of(
						Arrays.stream(new String[] {"First", "Second"})
							.map(s -> Paragraph.of(Collections.singletonList(Word.of(s))))
							.collect(Collectors.toList())
						))), smp.parse("<messageML><ul><li>First</li><li>Second</li></ul></messageML>", null));
	}
	
	@Test
	public void testOrderedListMessage() throws Exception {
		Assertions.assertEquals(
			Message.of(
				Arrays.asList(
					OrderedList.of(
						Arrays.stream(new String[] {"First", "Second"})
							.map(s -> Paragraph.of(Collections.singletonList(Word.of(s))))
							.collect(Collectors.toList())
						))), smp.parse("<messageML><ol><li>First</li><li>Second</li></ol></messageML>", null));
	}
	
	@Test
	public void testHelpMessage() throws Exception {
		Assertions.assertEquals(
			Message.of(
					Arrays.stream(new String[] {"help"})
						.map(s -> Word.of(s))
						.collect(Collectors.toList())
						), smp.parse("<messageML>Help</messageML>", null));
	}
	
	@Test
	public void testTaggedMessage() throws Exception {
		EntityJson ej = entityJsonConverter.readValue("{\"0\":{\"id\":[{\"type\":\"com.symphony.user.userId\",\"value\":\"347583113331315\"}],\"type\":\"com.symphony.user.mention\"},\"1\":{\"id\":[{\"type\":\"com.symphony.user.userId\",\"value\":\"345315370604167\"}],\"type\":\"com.symphony.user.mention\"},\"2\":{\"id\":[{\"type\":\"com.symphony.user.userId\",\"value\":\"345315370598706\"}],\"type\":\"com.symphony.user.mention\"}}");
		Assertions.assertEquals(
			Message.of(
				Arrays.asList(
					Paragraph.of(Collections.emptyList()),
					Paragraph.of(Arrays.asList(
							Word.of("/help"), 
							new UserDef("347583113331315", "Rob Moffat", null),
							new UserDef("345315370604167", "Mark Mainwood", null),
							new UserDef("345315370598706", "James Tan", null))))), 
			smp.parse(
				"<div data-format=\"PresentationML\" data-version=\"2.0\" class=\"wysiwyg\"><p> </p><p>/help <span class=\"entity\" data-entity-id=\"0\">@Rob Moffat</span> <span class=\"entity\" data-entity-id=\"1\">@Mark Mainwood</span> <span class=\"entity\" data-entity-id=\"2\">@James Tan</span> </p></div>",
				ej));
	}
	

}
