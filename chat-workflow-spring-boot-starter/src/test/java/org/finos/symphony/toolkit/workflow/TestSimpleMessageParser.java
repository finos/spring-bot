package org.finos.symphony.toolkit.workflow;

import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

import org.finos.symphony.toolkit.json.EntityJson;
import org.finos.symphony.toolkit.workflow.content.Content;
import org.finos.symphony.toolkit.workflow.content.Message;
import org.finos.symphony.toolkit.workflow.content.Paragraph;
import org.finos.symphony.toolkit.workflow.content.PastedTable;
import org.finos.symphony.toolkit.workflow.content.UserDef;
import org.finos.symphony.toolkit.workflow.content.Word;
import org.finos.symphony.toolkit.workflow.sources.symphony.handlers.EntityJsonConverter;
import org.finos.symphony.toolkit.workflow.sources.symphony.messages.SimpleMessageParser;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class TestSimpleMessageParser extends AbstractMockSymphonyTest {

	SimpleMessageParser smp = new SimpleMessageParser();
	
	@Autowired
	EntityJsonConverter entityJsonConverter;
		
	@Test
	public void testMessageWithTable() throws Exception {
		Content c = smp.parseMessage(
				"<div data-format=\"PresentationML\" data-version=\"2.0\" class=\"wysiwyg\"><p><table class=\"pasted-table\"><thead><tr><th>Name</th><th>Age</th><th>Alive</th></tr></thead><tbody><tr><td>Jim</td><td>5</td><td>FALSE</td></tr><tr><td>James</td><td>7</td><td>TRUE</td></tr></tbody></table></p></div>",
				new EntityJson());
		
		Assert.assertEquals(Paragraph.of(Arrays.asList(Word.of("Age"))), c.getNth(PastedTable.class, 0).get().getColumnNames().get(1));

		Assert.assertEquals(Paragraph.of(Arrays.asList(Word.of("TRUE"))), c.getNth(PastedTable.class, 0).get().getData().get(1).get(2));

	}
	
	@Test
	public void testSimpleMessage() throws Exception {
		Assert.assertEquals(
			Message.of(
				Arrays.asList(
					Paragraph.of(
						Arrays.stream(new String[] {"this", "is", "it"})
							.map(s -> Word.of(s))
							.collect(Collectors.toList())
						))), smp.parseMessage("<messageML><p>this is it</p></messageML>", null));
	}
	
	@Test
	public void testTaggedMessage() throws Exception {
		EntityJson ej = entityJsonConverter.readValue("{\"0\":{\"id\":[{\"type\":\"com.symphony.user.userId\",\"value\":\"347583113331315\"}],\"type\":\"com.symphony.user.mention\"},\"1\":{\"id\":[{\"type\":\"com.symphony.user.userId\",\"value\":\"345315370604167\"}],\"type\":\"com.symphony.user.mention\"},\"2\":{\"id\":[{\"type\":\"com.symphony.user.userId\",\"value\":\"345315370598706\"}],\"type\":\"com.symphony.user.mention\"}}");
		Assert.assertEquals(
			Message.of(
				Arrays.asList(
					Paragraph.of(Collections.emptyList()),
					Paragraph.of(Arrays.asList(
							Word.of("/help"), 
							new UserDef("347583113331315", "Rob Moffat", null),
							new UserDef("345315370604167", "Mark Mainwood", null),
							new UserDef("345315370598706", "James Tan", null))))), 
			smp.parseMessage(
				"<div data-format=\"PresentationML\" data-version=\"2.0\" class=\"wysiwyg\"><p> </p><p>/help <span class=\"entity\" data-entity-id=\"0\">@Rob Moffat</span> <span class=\"entity\" data-entity-id=\"1\">@Mark Mainwood</span> <span class=\"entity\" data-entity-id=\"2\">@James Tan</span> </p></div>",
				ej));
	}
	

}
