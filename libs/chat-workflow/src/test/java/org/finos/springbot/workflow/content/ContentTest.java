package org.finos.springbot.workflow.content;

import java.util.Arrays;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ContentTest {

	@Test
	public void testContentEquals() {
		Message m1 = createMessage();
		Message m2 = createMessage();
		Assertions.assertEquals(m1.hashCode(), m2.hashCode());
	}

	public static Message createMessage() {
		return Message.of(
			Heading.of("Heading 1", 1),	
			Paragraph.of("some words"),
			Paragraph.of("Some other words"),
			UnorderedList.of(
				Paragraph.of("item 1"),
				Paragraph.of("item 2")),
			BlockQuote.of("Something wicked this way comes"),
			Image.of("https://www.bob.com/image", "Some image"),
			Link.of("http://www.some.link", "This is a link"),
			Heading.of("Heading 2", 2),
			Table.of(Arrays.asList(Word.of("Heading 1"), Word.of("Heading 2")), 
					Arrays.asList(
						Arrays.asList(Word.of("value 1"), Word.of("value 2")),
						Arrays.asList(Word.of("value 3"), Word.of("value 4")),
						Arrays.asList(Word.of("value 5"), Word.of("value 6")))));
	}

}
