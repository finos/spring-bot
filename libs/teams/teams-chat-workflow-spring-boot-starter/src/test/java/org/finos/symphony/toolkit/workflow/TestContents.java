package org.finos.symphony.toolkit.workflow;

import java.util.Arrays;
import java.util.UUID;

import org.finos.symphony.toolkit.workflow.content.Content;
import org.finos.symphony.toolkit.workflow.content.Message;
import org.finos.symphony.toolkit.workflow.content.Paragraph;
import org.finos.symphony.toolkit.workflow.content.Word;
import org.finos.symphony.toolkit.workflow.sources.symphony.content.CashTag;
import org.finos.symphony.toolkit.workflow.sources.symphony.content.HashTag;
import org.finos.symphony.toolkit.workflow.sources.symphony.content.SymphonyRoom;
import org.finos.symphony.toolkit.workflow.sources.symphony.content.SymphonyUser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class TestContents {

	@Test
	public void testContents() {
		// tag def
		doAssertsOnContent(new CashTag("id123"), new CashTag("id123"));
		doAssertsOnContent(new HashTag("id123"), new HashTag("id123"));
		
		// room def
		doAssertsOnObject(new SymphonyRoom("abc", "abc123"), new SymphonyRoom("abc", "abc123"));
		doAssertsOnObject(new SymphonyRoom(null, "abc123"), new SymphonyRoom(null, "abc123"));
		doAssertsOnObject(new SymphonyRoom("abc","abc123"), new SymphonyRoom("abc", "abc123"));
		doAssertsOnObject(new SymphonyRoom("abc", null), new SymphonyRoom("abc", null));

		// user def
		doAssertsOnObject(new SymphonyUser(123l, "rob", "rob@example.com"), new SymphonyUser(123l, "rob", "rob@example.com"));
		doAssertsOnObject(new SymphonyUser("rob", "rob@example.com"), new SymphonyUser("rob", "rob@example.com"));
		doAssertsOnObject(new SymphonyUser(null, "rob@example.com"), new SymphonyUser(null, "rob@example.com"));
		doAssertsOnObject(new SymphonyUser(123l, "rob", null), new SymphonyUser(123l, "rob", null));
		
		// id
		UUID some = UUID.randomUUID();
		doAssertsOnContent(HashTag.createID(some), HashTag.createID(some));
		
		// wordx
		Word w1 = Word.of("hello");
		Word w2 = Word.of("bye");
		doAssertsOnContent(w1, Word.of("hello"));
		
		// paragraph
		Paragraph p1 = Paragraph.of(Arrays.asList(w1, w2));
		Paragraph p2 = Paragraph.of(Arrays.asList(w1, w2));
		doAssertsOnContent(p1, p2);
		doAssertsOnContent(p1.getNth(Word.class, 0).get(), p2.getNth(Word.class, 0).get());
		
		// message
		Message m1 = Message.of(Arrays.asList(p1, p2));
		Message m2 = Message.of(Arrays.asList(p1, p2));
		doAssertsOnContent(m1, m2);
		
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
}
