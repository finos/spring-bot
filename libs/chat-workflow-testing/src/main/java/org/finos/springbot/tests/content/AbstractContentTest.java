package org.finos.springbot.tests.content;

import java.util.Arrays;

import org.finos.springbot.workflow.content.BlockQuote;
import org.finos.springbot.workflow.content.CodeBlock;
import org.finos.springbot.workflow.content.Content;
import org.finos.springbot.workflow.content.Heading;
import org.finos.springbot.workflow.content.Image;
import org.finos.springbot.workflow.content.Link;
import org.finos.springbot.workflow.content.Message;
import org.finos.springbot.workflow.content.OrderedList;
import org.finos.springbot.workflow.content.Paragraph;
import org.finos.springbot.workflow.content.Table;
import org.finos.springbot.workflow.content.UnorderedList;
import org.finos.springbot.workflow.content.Word;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AbstractContentTest {

	@Test
	public void testContentEquals() {
		Message m1 = createMessage();
		Message m2 = createMessage();
		doAssertsOnContent(m1, m2);
	}
	
	@Test
	public void testMatches() {
		Message m1 = createMessage();
		Message m2 = createMessage();
		Assertions.assertTrue(m1.matches(m2));
	}
	
	protected void doAssertsOnContent(Content td1, Content td2) {
		doAssertsOnObject(td1, td2);
		Assertions.assertEquals(td1.getText(), td2.getText());
	}

	protected void doAssertsOnObject(Object td1, Object td2) {
		Assertions.assertEquals(td1, td2);
		Assertions.assertEquals(td1.hashCode(), td2.hashCode());
		Assertions.assertEquals(td1.toString(), td2.toString());
		Assertions.assertNotEquals(td1, "lemon");
	}

	/**
	 * This should exhaustively cover all the different implementations of {@link Content}.
	 */
	public static Message createMessage() {
		return Message.of(
			Heading.of("Heading 1", 1),	
			Paragraph.of("some words"),
			Paragraph.of("Some other words"),
			UnorderedList.of(
				Paragraph.of("item a"),
				Paragraph.of("item b")),
			OrderedList.of(
						Paragraph.of("item 1"),
						Paragraph.of("item 2")),
			BlockQuote.of("Something wicked this way comes"),
			CodeBlock.of("<some>code</some>"),
			Image.of("https://symphony-consultancy.com/assets/images/bot.svg", "Some image"),
			Link.of("https://symphony-consultancy.com", "This is a link"),
			Heading.of("Heading 2", 2),
			Table.of(Arrays.asList(Word.of("Heading 1"), Word.of("Heading 2")), 
					Arrays.asList(
						Arrays.asList(Word.of("value 1"), Word.of("value 2")),
						Arrays.asList(Word.of("value 3"), Word.of("value 4")),
						Arrays.asList(Word.of("value 5"), Word.of("value 6")))));
	}

	@Test
	public void testWithout() {
		Word one = Word.of("one");
		Word two = Word.of("two");
		Word three = Word.of("three");
		Paragraph p1 = Paragraph.of(one, two, three);
		Paragraph p2 = Paragraph.of(one, three);
		Paragraph p1_ = Paragraph.of(one, two);
		Paragraph p2_ = Paragraph.of(one);
		Message m1 = Message.of(p1, p2);
		Message m2 = Message.of(p1_, p2_);
		
		
		doAssertsOnContent(
				m1.without(three),
				m2);
	}
	
	@Test
	public void testRemoveStart() {
		Word one = Word.of("one");
		Word two = Word.of("two");
		Word three = Word.of("three");
		Paragraph p1 = Paragraph.of(one, two, three);
		Paragraph p1_ = Paragraph.of(two, three);
		Message m1 = Message.of(p1);
		Message m2 = Message.of(p1_);
		
		
		doAssertsOnContent(
				m1.removeAtStart(one),
				m2);
	}
}
