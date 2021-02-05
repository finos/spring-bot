package org.finos.symphony.toolkit.workflow;

import java.util.Arrays;
import java.util.UUID;

import org.finos.symphony.toolkit.workflow.content.CashTagDef;
import org.finos.symphony.toolkit.workflow.content.Content;
import org.finos.symphony.toolkit.workflow.content.HashTagDef;
import org.finos.symphony.toolkit.workflow.content.ID;
import org.finos.symphony.toolkit.workflow.content.Message;
import org.finos.symphony.toolkit.workflow.content.Paragraph;
import org.finos.symphony.toolkit.workflow.content.RoomDef;
import org.finos.symphony.toolkit.workflow.content.UserDef;
import org.finos.symphony.toolkit.workflow.content.Word;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class TestContents {

	@Test
	public void testContents() {
		// tag def
		doAssertsOnContent(new CashTagDef("id123"), new CashTagDef("id123"));
		doAssertsOnContent(new HashTagDef("id123"), new HashTagDef("id123"));
		
		// room def
		doAssertsOnObject(new RoomDef("abc", "desc", true, "abc123"), new RoomDef("abc", "desc", true, "abc123"));
		doAssertsOnObject(new RoomDef(null, "desc", true, "abc123"), new RoomDef(null, "desc", true, "abc123"));
		doAssertsOnObject(new RoomDef("abc", null, true, "abc123"), new RoomDef("abc", null, true, "abc123"));
		doAssertsOnObject(new RoomDef("abc", "desc", true, null), new RoomDef("abc", "desc", true, null));

		// user def
		doAssertsOnObject(new UserDef("abc", "rob", "rob@example.com"), new UserDef("abc", "rob", "rob@example.com"));
		doAssertsOnObject(new UserDef(null, "rob", "rob@example.com"), new UserDef(null, "rob", "rob@example.com"));
		doAssertsOnObject(new UserDef("abc", null, "rob@example.com"), new UserDef("abc", null, "rob@example.com"));
		doAssertsOnObject(new UserDef("abc", "rob", null), new UserDef("abc", "rob", null));
		
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
