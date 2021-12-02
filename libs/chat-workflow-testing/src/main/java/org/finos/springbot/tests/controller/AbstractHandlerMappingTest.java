package org.finos.springbot.tests.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.finos.springbot.workflow.annotations.ChatRequest;
import org.finos.springbot.workflow.content.Chat;
import org.finos.springbot.workflow.content.CodeBlock;
import org.finos.springbot.workflow.content.Message;
import org.finos.springbot.workflow.content.Paragraph;
import org.finos.springbot.workflow.content.Table;
import org.finos.springbot.workflow.content.User;
import org.finos.springbot.workflow.content.Word;
import org.finos.springbot.workflow.java.mapping.ChatMapping;
import org.finos.springbot.workflow.java.mapping.ChatRequestChatHandlerMapping;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringExtension;


@ExtendWith(SpringExtension.class)
public abstract class AbstractHandlerMappingTest {
	
	public static final String BOT_NAME = "Dummy Bot";
	public static final String BOT_EMAIL = "dummybot@example.com";
	public static final long BOT_ID = 654321l;
	public static final String ROB_EXAMPLE_EMAIL = "rob@example.com";
	public static final long ROB_EXAMPLE_ID = 765l;
	public static final String ROB_NAME =  "Robert Moffat";
	public static final String CHAT_ID = "abc123";

	@Autowired
	OurController oc;
	
	@Autowired
	ChatRequestChatHandlerMapping hm;
	
	@Test
	public void checkMappings() throws Exception {
		Assertions.assertEquals(15, hm.getHandlerMethods().size());
		getMappingsFor(Message.of("list"));
	}

	protected abstract List<ChatMapping<ChatRequest>> getMappingsFor(Message s) throws Exception;
	
	protected abstract String getMessageData();
	
	protected abstract String getMessageContent();

	protected abstract void execute(String s) throws Exception;
	
	@Test
	public void checkWildcardMapping() throws Exception {
		List<ChatMapping<ChatRequest>> mapped = getMappingsFor(Message.of("ban zebedee"));
		Assertions.assertTrue(mapped.size()  == 1);
	}
	
	@Test
	public void checkHandlerExecutors() throws Exception {
		execute("ban zebedee");
		Assertions.assertEquals("banWord", oc.lastMethod);
		Assertions.assertEquals(1,  oc.lastArguments.size());
		Assertions.assertEquals(Word.of("zebedee"),oc.lastArguments.get(0));
	}
	
	@Test
	public void checkMethodCall() throws Exception {
		execute("list");
		Assertions.assertEquals("doCommand", oc.lastMethod);
		Assertions.assertEquals(1,  oc.lastArguments.size());
		Assertions.assertTrue(Message.class.isAssignableFrom(oc.lastArguments.get(0).getClass()));
	}
	
	@Test
	public void checkMethodCallWithChatVariables() throws Exception {
		execute("ban gaurav");
		Assertions.assertEquals("banWord", oc.lastMethod);
		Assertions.assertEquals(1,  oc.lastArguments.size());
		Object firstArgument = oc.lastArguments.get(0);
		Assertions.assertTrue(Word.class.isAssignableFrom(firstArgument.getClass()));
		Assertions.assertEquals("gaurav", ((Word)firstArgument).getText());
	}
	
	@Test
	public void testAuthorChatVariable() throws Exception {
		execute("userDetails2 @gaurav");
		Assertions.assertEquals("userDetails2", oc.lastMethod);
		Assertions.assertEquals(2,  oc.lastArguments.size());
		Object firstArgument = oc.lastArguments.get(0);
		Assertions.assertTrue(User.class.isAssignableFrom(firstArgument.getClass()));
		Assertions.assertEquals("gaurav", ((User)firstArgument).getName());
		
		Object secondArgument = oc.lastArguments.get(1);
		Assertions.assertTrue(User.class.isAssignableFrom(secondArgument.getClass()));
		Assertions.assertEquals("@"+ROB_NAME, ((User)secondArgument).getText());
	}
	
	@Test
	public void testUserChatVariable() throws Exception {
		execute("delete @gaurav");
		Assertions.assertEquals("removeUserFromRoom", oc.lastMethod);
		Assertions.assertEquals(2,  oc.lastArguments.size());
		Object firstArgument = oc.lastArguments.get(0);
		Assertions.assertTrue(User.class.isAssignableFrom(firstArgument.getClass()));
		Assertions.assertEquals("gaurav", ((User)firstArgument).getName());
		
		Object secondArgument = oc.lastArguments.get(1);
		Assertions.assertTrue(Chat.class.isAssignableFrom(secondArgument.getClass()));
		Assertions.assertEquals(OurController.SOME_ROOM, ((Chat)secondArgument).getName());
	}
	

	
	@Test
	public void testCodeblockMapping() throws Exception {
		execute("update <pre>public static void main(String[] args) {}</pre>");
		Assertions.assertEquals("process2", oc.lastMethod);
		Assertions.assertEquals(1,  oc.lastArguments.size());
		Object firstArgument = oc.lastArguments.get(0);
		Assertions.assertTrue(CodeBlock.class.isAssignableFrom(firstArgument.getClass()));
		Assertions.assertEquals("public static void main(String[] args) {}", ((CodeBlock)firstArgument).getText());
	}
	
	@Test
	public void testHelp() throws Exception {
		execute("help");
		assertHelpResponse();
	}

	protected abstract void assertHelpResponse() throws Exception;
	

	@Test
	public void testCodeblockMapping2() throws Exception {
		execute("update <code>public <a href=\"sfdkjfh\">nonsense</a>static void main(String[] args) {}</code>");
		Assertions.assertEquals("process2", oc.lastMethod);
		Assertions.assertEquals(1,  oc.lastArguments.size());
		Object firstArgument = oc.lastArguments.get(0);
		Assertions.assertTrue(CodeBlock.class.isAssignableFrom(firstArgument.getClass()));
		Assertions.assertEquals("public static void main(String[] args) {}", ((CodeBlock)firstArgument).getText());
	}
	
	

	@Test
	public void testTableMapping() throws Exception {
		execute("process-table <table>\n"
				+ "      <tr>\n"
				+ "        <th>Thing</th><th>Thang</th>\n"
				+ "      </tr>\n"
				+ "      <tr>\n"
				+ "        <td>1</td><td>2</td>\n"
				+ "      </tr>\n"
				+ "      <tr>\n"
				+ "        <td>3</td><td>4</td>\n"
				+ "      </tr>\n"
				+ "  </table> @gaurav");
		Assertions.assertEquals("process-table", oc.lastMethod);
		Assertions.assertEquals(2,  oc.lastArguments.size());
		Table firstArgument = (Table) oc.lastArguments.get(0);
		List<Paragraph> expected = Arrays.asList(Paragraph.of("thing"), Paragraph.of("thang"));
		Assertions.assertEquals(expected, firstArgument.getColumnNames());
		Assertions.assertEquals(2, firstArgument.getData().size());
	}
	
	@Test
	public void testMessageResponse() throws Exception {
		execute("ban rob");
		Assertions.assertEquals("banWord", oc.lastMethod);
		String message = getMessageContent();
		System.out.println(message);
		Assertions.assertTrue(message.contains("banned words: rob"));
	}
	
	@Test
	public void testFormResponse1() throws Exception {
		execute("form1");
		String data = getMessageData();
		Assertions.assertTrue(data.contains("\"id\" : \"go\"") || data.contains("\"name\" : \"go\""));
	}
	
	@Test
	public void testFormResponse2() throws Exception {
		execute("form2");
		assertNoButtons();
	}
	
	protected abstract void assertNoButtons();

	@Test
	public void testThrowsError() throws Exception {
		execute("throwsError");
		Assertions.assertTrue(getMessageData().contains("Error123"));
	}
	
	@Test
	public void testOptionalPresent() throws Exception {
		execute("optionals zib zab zob @gaurav pingu");
		Assertions.assertEquals("doList", oc.lastMethod);
		Assertions.assertEquals(3,  oc.lastArguments.size());
		
		Object firstArgument = oc.lastArguments.get(1);
		Assertions.assertEquals("gaurav", ((Optional<User>)firstArgument).get().getName());
		
		Object secondArgument = oc.lastArguments.get(0);
		Assertions.assertEquals(secondArgument, Arrays.asList(Word.of("zib"), Word.of("zab"), Word.of("zob")));
		
		Object thirdArgument = oc.lastArguments.get(2);
		Assertions.assertEquals(Word.of("pingu"), thirdArgument);
	}
	
	@Test
	public void testOptionalMissing() throws Exception {
		execute("optionals");
		Assertions.assertEquals("doList", oc.lastMethod);
		Assertions.assertEquals(3,  oc.lastArguments.size());
		Object firstArgument = oc.lastArguments.get(0);
		Assertions.assertTrue(firstArgument instanceof List);
		Assertions.assertEquals(0, ((List<?>)firstArgument).size());
		
		Object secondArgument = oc.lastArguments.get(1);
		Assertions.assertTrue(secondArgument instanceof Optional);
		Assertions.assertFalse(((Optional<?>)secondArgument).isPresent());
		
		Assertions.assertNull(oc.lastArguments.get(2));
		
	}
	
	protected abstract void pressButton(String b, Map<String, Object> contents);
	
	@Test
	public void testButtonPress() throws Exception {
		Map<String, Object> form = new HashMap<>();
		form.put("amount", 45f);
		form.put("description", "desc");
		form.put("form", StartClaim.class.getName());
		
		pressButton(OurController.class.getName()+"-startNewClaim", form);
		Assertions.assertEquals("startNewClaim", oc.lastMethod);
		StartClaim sc = (StartClaim) oc.lastArguments.get(0);
		Assertions.assertEquals(45f, sc.amount);
		Assertions.assertEquals("desc", sc.description);
		
	}

}
