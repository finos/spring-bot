package org.finos.springbot.symphony.content;

import java.io.IOException;
import java.nio.charset.Charset;

import org.finos.springbot.tests.content.AbstractContentTest;
import org.finos.springbot.workflow.content.Message;
import org.finos.springbot.workflow.content.serialization.MarkupWriter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StreamUtils;

@SpringBootTest(classes = { SymphonyContentConfig.class, })
public class SymphonyContentTest extends AbstractContentTest {

	@Autowired
	MarkupWriter mw;

	@Test
	public void testSymphonyContents() throws IOException {
		// tag def
		doAssertsOnContent(new CashTag("id123"), new CashTag("id123"));
		doAssertsOnContent(new HashTag("id123"), new HashTag("id123"));

		// room def
		doAssertsOnObject(new SymphonyRoom("abc", "abc123"), new SymphonyRoom("abc", "abc123"));
		doAssertsOnObject(new SymphonyRoom(null, "abc123"), new SymphonyRoom(null, "abc123"));
		doAssertsOnObject(new SymphonyRoom("abc", "abc123"), new SymphonyRoom("abc", "abc123"));
		doAssertsOnObject(new SymphonyRoom("abc", null), new SymphonyRoom("abc", null));

		// user def
		doAssertsOnObject(new SymphonyUser(123l, "rob", "rob@example.com"),
				new SymphonyUser(123l, "rob", "rob@example.com"));
		doAssertsOnObject(new SymphonyUser("rob", "rob@example.com"), new SymphonyUser("rob", "rob@example.com"));
		doAssertsOnObject(new SymphonyUser(null, "rob@example.com"), new SymphonyUser(null, "rob@example.com"));
		doAssertsOnObject(new SymphonyUser(123l, "rob", null), new SymphonyUser(123l, "rob", null));

		
		Message m = Message.of(
			new CashTag("id123"), 
			new HashTag("ht123"), 
			new SymphonyUser("RR","rr@example.com"));
		
		String out = mw.apply(m);
		System.out.println(out);
		Assertions.assertEquals(load("testSymphonyContents.html"), out);		
	}

	@Test
	public void testWriteContent() throws IOException {
		String out = mw.apply(createMessage());
		System.out.println(out);
		Assertions.assertEquals(load("testWriteContent.html"), out);
	}

	private String load(String string) throws IOException {
		return StreamUtils
				.copyToString(SymphonyContentTest.class.getResourceAsStream(string), Charset.forName("UTF-8"))
				.replace("\r\n", "\n");
	}
}
