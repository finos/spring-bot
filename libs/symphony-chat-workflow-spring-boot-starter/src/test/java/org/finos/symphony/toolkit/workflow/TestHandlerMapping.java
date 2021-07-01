package org.finos.symphony.toolkit.workflow;

import java.util.Arrays;
import java.util.List;

import org.finos.symphony.toolkit.json.EntityJson;
import org.finos.symphony.toolkit.workflow.actions.Action;
import org.finos.symphony.toolkit.workflow.actions.SimpleMessageAction;
import org.finos.symphony.toolkit.workflow.annotations.ChatVariable;
import org.finos.symphony.toolkit.workflow.annotations.Exposed;
import org.finos.symphony.toolkit.workflow.content.CodeBlock;
import org.finos.symphony.toolkit.workflow.content.HashTag;
import org.finos.symphony.toolkit.workflow.content.HashTagDef;
import org.finos.symphony.toolkit.workflow.content.Message;
import org.finos.symphony.toolkit.workflow.content.Paragraph;
import org.finos.symphony.toolkit.workflow.content.PastedTable;
import org.finos.symphony.toolkit.workflow.content.Room;
import org.finos.symphony.toolkit.workflow.content.RoomDef;
import org.finos.symphony.toolkit.workflow.content.User;
import org.finos.symphony.toolkit.workflow.content.UserDef;
import org.finos.symphony.toolkit.workflow.content.Word;
import org.finos.symphony.toolkit.workflow.fixture.OurController;
import org.finos.symphony.toolkit.workflow.history.History;
import org.finos.symphony.toolkit.workflow.java.converters.ResponseConverter;
import org.finos.symphony.toolkit.workflow.java.mapping.ChatHandlerExecutor;
import org.finos.symphony.toolkit.workflow.java.mapping.ChatMapping;
import org.finos.symphony.toolkit.workflow.java.mapping.ExposedHandlerMapping;
import org.finos.symphony.toolkit.workflow.java.resolvers.AddressableWorkflowResolverFactory;
import org.finos.symphony.toolkit.workflow.java.resolvers.ChatVariableWorkflowResolverFactory;
import org.finos.symphony.toolkit.workflow.java.resolvers.ResolverConfig;
import org.finos.symphony.toolkit.workflow.java.resolvers.WorkflowResolversFactory;
import org.finos.symphony.toolkit.workflow.response.AttachmentResponse;
import org.finos.symphony.toolkit.workflow.response.FormResponse;
import org.finos.symphony.toolkit.workflow.response.MessageResponse;
import org.finos.symphony.toolkit.workflow.response.ResponseHandler;
import org.finos.symphony.toolkit.workflow.sources.symphony.messages.MessagePartWorkflowResolverFactory;
import org.finos.symphony.toolkit.workflow.sources.symphony.messages.SimpleMessageParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringExtension;


@SpringBootTest(classes = {
		TestHandlerMapping.TestConfig.class	,
		ResolverConfig.class
})
@ExtendWith(SpringExtension.class)
public class TestHandlerMapping {

	@Autowired
	OurController oc;
	
	@Autowired
	ExposedHandlerMapping hm;
	
	@MockBean
	History h;
	
	@MockBean
	ResponseHandler rh;

	@Configuration
	static class TestConfig {
		
		
		@Bean
		public OurController ourController() {
			return new OurController();
		}
		
		@Bean
		public ExposedHandlerMapping handlerMapping(WorkflowResolversFactory wrf, ResponseHandler rh, List<ResponseConverter> converters) {
			return new ExposedHandlerMapping(wrf, rh, converters);
		}
		
		@Bean
		public MessagePartWorkflowResolverFactory messagePartWorkflowResolverFactory() {
			return new MessagePartWorkflowResolverFactory();
		}
		
		@Bean
		public ChatVariableWorkflowResolverFactory chatVariableWorkflowResolverFactory() {
			return new ChatVariableWorkflowResolverFactory();
		}

		@Bean
		public AddressableWorkflowResolverFactory addressableWorkflowResolverFactory() {
			return new AddressableWorkflowResolverFactory();
		}

		@Bean
		public SimpleMessageParser simpleMessageParser() {
			return new SimpleMessageParser();
		}
		
	}
	
	@Autowired
	SimpleMessageParser smp;
	
	@Test
	public void checkMappings() throws Exception {
		Assertions.assertEquals(14, hm.getHandlerMethods().size());
		getMappingsFor("list");
	}

	private List<ChatMapping<Exposed>> getMappingsFor(String s) throws Exception {
		EntityJson jsonObjects = new EntityJson();
		Message m = smp.parse("<messageML>"+s+"</messageML>", jsonObjects);
		Action a = new SimpleMessageAction(null, null, null, m, jsonObjects);
		return hm.getHandlers(a);
	}
	

	private List<ChatHandlerExecutor> getExecutorsFor(String s) throws Exception {
		EntityJson jsonObjects = new EntityJson();
		jsonObjects.put("1", new UserDef("1", "gaurav", "gaurav@example.com"));
		jsonObjects.put("2", new HashTagDef("SomeTopic"));
		Message m = smp.parse("<messageML>"+s+"</messageML>", jsonObjects);
		Room r = new RoomDef("The Room Where It Happened", "Some description", true, "abc123");
		User author = new UserDef("user123", "Rob Moffat", "rob.moffat@example.com");
		Action a = new SimpleMessageAction(null, r, author, m, jsonObjects);
		return hm.getExecutors(a);
	}
	
	@Test
	public void checkWildcardMapping() throws Exception {
		List<ChatMapping<Exposed>> mapped = getMappingsFor("ban zebedee");
		Assertions.assertTrue(mapped.size()  == 1);
	}
	
	@Test
	public void checkHandlerExecutors() throws Exception {
		List<ChatHandlerExecutor> mapped = getExecutorsFor("ban zebedee");
		Assertions.assertTrue(mapped.size()  == 1);
		
		ChatHandlerExecutor first = mapped.get(0);
		ChatVariable firstKey = first.getReplacements().keySet().iterator().next();
		
		Assertions.assertEquals("word", firstKey.value());
		Assertions.assertEquals(Word.of("zebedee"),first.getReplacements().get(firstKey));
	}
	
	@Test
	public void checkMethodCall() throws Exception {
		List<ChatHandlerExecutor> mapped = getExecutorsFor("list");
		Assertions.assertTrue(mapped.size()  == 1);
		mapped.stream().forEach(e -> e.execute());
		
		Assertions.assertEquals("doCommand", oc.lastMethod);
		Assertions.assertEquals(1,  oc.lastArguments.size());
		Assertions.assertTrue(Message.class.isAssignableFrom(oc.lastArguments.get(0).getClass()));
		
	}
	
	
	@Test
	public void checkMethodCallWithChatVariables() throws Exception {
		List<ChatHandlerExecutor> mapped = getExecutorsFor("ban gaurav");
		Assertions.assertTrue(mapped.size()  == 1);
		mapped.stream().forEach(e -> e.execute());
		
		Assertions.assertEquals("banWord", oc.lastMethod);
		Assertions.assertEquals(1,  oc.lastArguments.size());
		Object firstArgument = oc.lastArguments.get(0);
		Assertions.assertTrue(Word.class.isAssignableFrom(firstArgument.getClass()));
		Assertions.assertEquals("gaurav", ((Word)firstArgument).getText());
	}
	
	@Test
	public void testUserChatVariable() throws Exception {
		List<ChatHandlerExecutor> mapped = getExecutorsFor("delete <span class=\"entity\" data-entity-id=\"1\">@gaurav</span>");
		Assertions.assertTrue(mapped.size()  == 1);
		mapped.stream().forEach(e -> e.execute());
		
		Assertions.assertEquals("removeUserFromRoom", oc.lastMethod);
		Assertions.assertEquals(2,  oc.lastArguments.size());
		Object firstArgument = oc.lastArguments.get(0);
		Assertions.assertTrue(User.class.isAssignableFrom(firstArgument.getClass()));
		Assertions.assertEquals("gaurav", ((User)firstArgument).getName());
		
		Object secondArgument = oc.lastArguments.get(1);
		Assertions.assertTrue(Room.class.isAssignableFrom(secondArgument.getClass()));
		Assertions.assertEquals("The Room Where It Happened", ((Room)secondArgument).getRoomName());
	}
	
	@Test
	public void testHashtagMapping() throws Exception {
		List<ChatHandlerExecutor> mapped = getExecutorsFor("add <span class=\"entity\" data-entity-id=\"1\">@gaurav</span> to <span class=\"entity\" data-entity-id=\"2\">#SomeTopic</span>");
		Assertions.assertTrue(mapped.size()  == 1);
		mapped.stream().forEach(e -> e.execute());
		
		Assertions.assertEquals("addUserToTopic", oc.lastMethod);
		Assertions.assertEquals(2,  oc.lastArguments.size());
		Object firstArgument = oc.lastArguments.get(0);
		Assertions.assertTrue(User.class.isAssignableFrom(firstArgument.getClass()));
		Assertions.assertEquals("gaurav", ((User)firstArgument).getName());
		
		Object secondArgument = oc.lastArguments.get(1);
		Assertions.assertTrue(HashTag.class.isAssignableFrom(secondArgument.getClass()));
		Assertions.assertEquals("SomeTopic", ((HashTag)secondArgument).getName());
	}
	
	
	@Test
	public void testCodeblockMapping() throws Exception {
		List<ChatHandlerExecutor> mapped = getExecutorsFor("update <pre>public static void main(String[] args) {}</pre>");
		Assertions.assertTrue(mapped.size()  == 1);
		mapped.stream().forEach(e -> e.execute());
		
		Assertions.assertEquals("process2", oc.lastMethod);
		Assertions.assertEquals(1,  oc.lastArguments.size());
		Object firstArgument = oc.lastArguments.get(0);
		Assertions.assertTrue(CodeBlock.class.isAssignableFrom(firstArgument.getClass()));
		Assertions.assertEquals("public static void main(String[] args) {}", ((CodeBlock)firstArgument).getText());
	}
	

	@Test
	public void testCodeblockMapping2() throws Exception {
		List<ChatHandlerExecutor> mapped = getExecutorsFor("update <code>public <a href=\"sfdkjfh\">nonsense</a>static void main(String[] args) {}</code>");
		Assertions.assertTrue(mapped.size()  == 1);
		mapped.stream().forEach(e -> e.execute());
		
		Assertions.assertEquals("process2", oc.lastMethod);
		Assertions.assertEquals(1,  oc.lastArguments.size());
		Object firstArgument = oc.lastArguments.get(0);
		Assertions.assertTrue(CodeBlock.class.isAssignableFrom(firstArgument.getClass()));
		Assertions.assertEquals("public static void main(String[] args) {}", ((CodeBlock)firstArgument).getText());
	}
	
	

	@Test
	public void testTableMapping() throws Exception {
		List<ChatHandlerExecutor> mapped = getExecutorsFor("process-table <table>\n"
				+ "      <tr>\n"
				+ "        <th>Thing</th><th>Thang</th>\n"
				+ "      </tr>\n"
				+ "      <tr>\n"
				+ "        <td>1</td><td>2</td>\n"
				+ "      </tr>\n"
				+ "      <tr>\n"
				+ "        <td>3</td><td>4</td>\n"
				+ "      </tr>\n"
				+ "  </table> <span class=\"entity\" data-entity-id=\"1\">@gaurav</span>");
		Assertions.assertTrue(mapped.size()  == 1);
		mapped.stream().forEach(e -> e.execute());
		
		Assertions.assertEquals("process-table", oc.lastMethod);
		Assertions.assertEquals(2,  oc.lastArguments.size());
		PastedTable firstArgument = (PastedTable) oc.lastArguments.get(0);
		List<Paragraph> expected = Arrays.asList(Paragraph.of(Arrays.asList(Word.of("thing"))), Paragraph.of(Arrays.asList(Word.of("thang"))));
		Assertions.assertEquals(expected, firstArgument.getColumnNames());
		Assertions.assertEquals(2, firstArgument.getData().size());
	}
	
	@Test
	public void testMessageResponse() throws Exception {
		List<ChatHandlerExecutor> mapped = getExecutorsFor("ban rob");
		Assertions.assertTrue(mapped.size()  == 1);
		mapped.stream().forEach(e -> e.execute());
		
		Assertions.assertEquals("banWord", oc.lastMethod);
		
		Mockito.verify(rh).accept(Mockito.any(MessageResponse.class));
		Mockito.clearInvocations();
	}
	
	@Test
	public void testAttachmentResponse() throws Exception {
		List<ChatHandlerExecutor> mapped = getExecutorsFor("attachment");
		Assertions.assertTrue(mapped.size()  == 1);
		mapped.stream().forEach(e -> e.execute());

		Mockito.verify(rh).accept(Mockito.any(AttachmentResponse.class));
		Mockito.clearInvocations();
	}
	
	@Test
	public void testFormResponse1() throws Exception {
		List<ChatHandlerExecutor> mapped = getExecutorsFor("form1");
		Assertions.assertTrue(mapped.size()  == 1);
		mapped.stream().forEach(e -> e.execute());
		Mockito.verify(rh).accept(Mockito.any(FormResponse.class));
		Mockito.clearInvocations();
	}
	
	@Test
	public void testFormResponse2() throws Exception {
		List<ChatHandlerExecutor> mapped = getExecutorsFor("form2");
		Assertions.assertTrue(mapped.size()  == 1);
		mapped.stream().forEach(e -> e.execute());
		Mockito.verify(rh).accept(Mockito.any(FormResponse.class));
		Mockito.clearInvocations();
	}
	


}
