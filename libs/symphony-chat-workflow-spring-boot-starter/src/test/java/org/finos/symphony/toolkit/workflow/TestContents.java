package org.finos.symphony.toolkit.workflow;

import java.util.Arrays;
import java.util.UUID;

import org.finos.symphony.toolkit.workflow.content.Content;
import org.finos.symphony.toolkit.workflow.content.Message;
import org.finos.symphony.toolkit.workflow.content.Paragraph;
import org.finos.symphony.toolkit.workflow.content.Word;
import org.finos.symphony.toolkit.workflow.sources.symphony.content.CashTagDef;
import org.finos.symphony.toolkit.workflow.sources.symphony.content.HashTagDef;
import org.finos.symphony.toolkit.workflow.sources.symphony.content.ID;
import org.finos.symphony.toolkit.workflow.sources.symphony.content.SymphonyRoom;
import org.finos.symphony.toolkit.workflow.sources.symphony.content.SymphonyUser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class TestContents {

	@Test
	public void testContents() {
		// tag def
		doAssertsOnContent(new CashTagDef("id123"), new CashTagDef("id123"));
		doAssertsOnContent(new HashTagDef("id123"), new HashTagDef("id123"));
		
		// room def
		doAssertsOnObject(new SymphonyRoom("abc", "desc", true, "abc123"), new SymphonyRoom("abc", "desc", true, "abc123"));
		doAssertsOnObject(new SymphonyRoom(null, "desc", true, "abc123"), new SymphonyRoom(null, "desc", true, "abc123"));
		doAssertsOnObject(new SymphonyRoom("abc", null, true, "abc123"), new SymphonyRoom("abc", null, true, "abc123"));
		doAssertsOnObject(new SymphonyRoom("abc", "desc", true, null), new SymphonyRoom("abc", "desc", true, null));

		// user def
		doAssertsOnObject(new SymphonyUser("abc", "rob", "rob@example.com"), new SymphonyUser("abc", "rob", "rob@example.com"));
		doAssertsOnObject(new SymphonyUser(null, "rob", "rob@example.com"), new SymphonyUser(null, "rob", "rob@example.com"));
		doAssertsOnObject(new SymphonyUser("abc", null, "rob@example.com"), new SymphonyUser("abc", null, "rob@example.com"));
		doAssertsOnObject(new SymphonyUser("abc", "rob", null), new SymphonyUser("abc", "rob", null));
		
		// id
		UUID some = UUID.randomUUID();
		doAssertsOnContent(new ID(some), new ID(some));
		
		// word
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
