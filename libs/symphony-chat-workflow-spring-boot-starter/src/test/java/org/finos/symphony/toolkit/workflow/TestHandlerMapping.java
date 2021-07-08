package org.finos.symphony.toolkit.workflow;

import java.util.Arrays;
import java.util.List;

import org.finos.symphony.toolkit.json.EntityJson;
import org.finos.symphony.toolkit.spring.api.SymphonyApiConfig;
import org.finos.symphony.toolkit.workflow.actions.Action;
import org.finos.symphony.toolkit.workflow.actions.SimpleMessageAction;
import org.finos.symphony.toolkit.workflow.annotations.Exposed;
import org.finos.symphony.toolkit.workflow.content.Chat;
import org.finos.symphony.toolkit.workflow.content.CodeBlock;
import org.finos.symphony.toolkit.workflow.content.Message;
import org.finos.symphony.toolkit.workflow.content.Paragraph;
import org.finos.symphony.toolkit.workflow.content.Table;
import org.finos.symphony.toolkit.workflow.content.User;
import org.finos.symphony.toolkit.workflow.content.Word;
import org.finos.symphony.toolkit.workflow.fixture.OurController;
import org.finos.symphony.toolkit.workflow.history.History;
import org.finos.symphony.toolkit.workflow.java.mapping.ChatMapping;
import org.finos.symphony.toolkit.workflow.java.mapping.ExposedHandlerMapping;
import org.finos.symphony.toolkit.workflow.java.resolvers.ResolverConfig;
import org.finos.symphony.toolkit.workflow.message.MethodCallMessageConsumer;
import org.finos.symphony.toolkit.workflow.response.AttachmentResponse;
import org.finos.symphony.toolkit.workflow.response.ErrorResponse;
import org.finos.symphony.toolkit.workflow.response.FormResponse;
import org.finos.symphony.toolkit.workflow.response.MessageResponse;
import org.finos.symphony.toolkit.workflow.response.handlers.AttachmentHandler;
import org.finos.symphony.toolkit.workflow.sources.symphony.SymphonyWorkflowConfig;
import org.finos.symphony.toolkit.workflow.sources.symphony.content.HashTag;
import org.finos.symphony.toolkit.workflow.sources.symphony.content.HashTagDef;
import org.finos.symphony.toolkit.workflow.sources.symphony.content.SymphonyRoom;
import org.finos.symphony.toolkit.workflow.sources.symphony.content.SymphonyUser;
import org.finos.symphony.toolkit.workflow.sources.symphony.handlers.SymphonyResponseHandler2;
import org.finos.symphony.toolkit.workflow.sources.symphony.handlers.freemarker.FreemarkerTypeConverterConfig;
import org.finos.symphony.toolkit.workflow.sources.symphony.handlers.jersey.JerseyAttachmentHandlerConfig;
import org.finos.symphony.toolkit.workflow.sources.symphony.messages.MessageMLParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.validation.Validator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.symphony.api.agent.MessagesApi;
import com.symphony.api.id.SymphonyIdentity;
import com.symphony.api.pod.RoomMembershipApi;
import com.symphony.api.pod.StreamsApi;
import com.symphony.api.pod.UsersApi;


@SpringBootTest(classes = {
		TestHandlerMapping.TestConfig.class	,
		ChatWorkflowConfig.class,
		ResolverConfig.class,
		SymphonyWorkflowConfig.class,
		FreemarkerTypeConverterConfig.class
})
@ExtendWith(SpringExtension.class)
public class TestHandlerMapping {

	@Autowired
	OurController oc;
	
	@Autowired
	ExposedHandlerMapping hm;
	
	@MockBean
	History h;
	
	@MockBean(name = SymphonyApiConfig.SINGLE_BOT_IDENTITY_BEAN)
	SymphonyIdentity botIdentity;
	
	@MockBean
	UsersApi usersApi;
	
	@MockBean
	MessagesApi messagesApi; 
	
	@MockBean
	RoomMembershipApi roomMembershipApi;
	
	@MockBean
	StreamsApi streamsApi;
	
	@MockBean
	Validator validator;

	
	@Autowired
	SymphonyResponseHandler2 rh;
	
	@Autowired
	MethodCallMessageConsumer mc;

	@Configuration
	static class TestConfig {
		 
		
		@Bean
		public OurController ourController() {
			return new OurController();
		}

		@Bean
		public MessageMLParser simpleMessageParser() {
			return new MessageMLParser();
		}
		
		@Bean
		public ObjectMapper objectMapper() {
			return new ObjectMapper();
		}
		
		@Bean
		public AttachmentHandler symphonyAttachmentHandler() {
			return new AttachmentHandler() {
				
				@Override
				public Object formatAttachment(AttachmentResponse ar) {
					// TODO Auto-generated method stub
					return null;
				}
			};
		}
		
		
	}
	
	@Autowired
	MessageMLParser smp;
	
	@BeforeEach
	public void setupMocks() {
		Mockito.when(usersApi.v1UserGet(
				Mockito.anyString(), 
				Mockito.nullable(String.class), 
				Mockito.anyBoolean()))
			.thenAnswer(a -> {
				Object out = new com.symphony.api.model.User().emailAddress("rob@example.com").id(1234l);
				return out;
			});
	}
	
	@Test
	public void checkMappings() throws Exception {
		Assertions.assertEquals(16, hm.getHandlerMethods().size());
		getMappingsFor("list");
	}

	private List<ChatMapping<Exposed>> getMappingsFor(String s) throws Exception {
		EntityJson jsonObjects = new EntityJson();
		Message m = smp.parse("<messageML>"+s+"</messageML>", jsonObjects);
		Action a = new SimpleMessageAction(null, null, m, jsonObjects);
		return hm.getHandlers(a);
	}
	

	private void execute(String s) throws Exception {
		EntityJson jsonObjects = new EntityJson();
		jsonObjects.put("1", new SymphonyUser("1", "gaurav", "gaurav@example.com"));
		jsonObjects.put("2", new HashTagDef("SomeTopic"));
		Message m = smp.parse("<messageML>"+s+"</messageML>", jsonObjects);
		Chat r = new SymphonyRoom("The Room Where It Happened", "Some description", true, "abc123");
		User author = new SymphonyUser("user123", "Rob Moffat", "rob.moffat@example.com");
		Action a = new SimpleMessageAction(r, author, m, jsonObjects);
		mc.accept(a);
	}
	
	@Test
	public void checkWildcardMapping() throws Exception {
		List<ChatMapping<Exposed>> mapped = getMappingsFor("ban zebedee");
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
		execute("userDetails2 <span class=\"entity\" data-entity-id=\"1\">@gaurav</span>");
		Assertions.assertEquals("userDetails2", oc.lastMethod);
		Assertions.assertEquals(2,  oc.lastArguments.size());
		Object firstArgument = oc.lastArguments.get(0);
		Assertions.assertTrue(User.class.isAssignableFrom(firstArgument.getClass()));
		Assertions.assertEquals("gaurav", ((User)firstArgument).getName());
		
		Object secondArgument = oc.lastArguments.get(1);
		Assertions.assertTrue(User.class.isAssignableFrom(secondArgument.getClass()));
		Assertions.assertEquals("rob.moffat@example.com", ((User)secondArgument).getAddress());
	}
	
	@Test
	public void testUserChatVariable() throws Exception {
		execute("delete <span class=\"entity\" data-entity-id=\"1\">@gaurav</span>");
		Assertions.assertEquals("removeUserFromRoom", oc.lastMethod);
		Assertions.assertEquals(2,  oc.lastArguments.size());
		Object firstArgument = oc.lastArguments.get(0);
		Assertions.assertTrue(User.class.isAssignableFrom(firstArgument.getClass()));
		Assertions.assertEquals("gaurav", ((User)firstArgument).getName());
		
		Object secondArgument = oc.lastArguments.get(1);
		Assertions.assertTrue(Chat.class.isAssignableFrom(secondArgument.getClass()));
		Assertions.assertEquals("The Room Where It Happened", ((Chat)secondArgument).getName());
	}
	
	@Test
	public void testHashtagMapping() throws Exception {
		execute("add <span class=\"entity\" data-entity-id=\"1\">@gaurav</span> to <span class=\"entity\" data-entity-id=\"2\">#SomeTopic</span>");
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
		execute("update <pre>public static void main(String[] args) {}</pre>");
		Assertions.assertEquals("process2", oc.lastMethod);
		Assertions.assertEquals(1,  oc.lastArguments.size());
		Object firstArgument = oc.lastArguments.get(0);
		Assertions.assertTrue(CodeBlock.class.isAssignableFrom(firstArgument.getClass()));
		Assertions.assertEquals("public static void main(String[] args) {}", ((CodeBlock)firstArgument).getText());
	}
	

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
				+ "  </table> <span class=\"entity\" data-entity-id=\"1\">@gaurav</span>");
		Assertions.assertEquals("process-table", oc.lastMethod);
		Assertions.assertEquals(2,  oc.lastArguments.size());
		Table firstArgument = (Table) oc.lastArguments.get(0);
		List<Paragraph> expected = Arrays.asList(Paragraph.of(Arrays.asList(Word.of("thing"))), Paragraph.of(Arrays.asList(Word.of("thang"))));
		Assertions.assertEquals(expected, firstArgument.getColumnNames());
		Assertions.assertEquals(2, firstArgument.getData().size());
	}
	
	@Test
	public void testMessageResponse() throws Exception {
		execute("ban rob");
		Assertions.assertEquals("banWord", oc.lastMethod);
		Mockito.verify(rh).accept(Mockito.any(MessageResponse.class));
		Mockito.clearInvocations();
	}
	
	@Test
	public void testAttachmentResponse() throws Exception {
		execute("attachment");
		Mockito.verify(rh).accept(Mockito.any(AttachmentResponse.class));
		Mockito.clearInvocations();
	}
	
	@Test
	public void testFormResponse1() throws Exception {
		execute("form1");
		Mockito.verify(rh).accept(Mockito.any(FormResponse.class));
		Mockito.clearInvocations();
	}
	
	@Test
	public void testFormResponse2() throws Exception {
		execute("form2");
		Mockito.verify(rh).accept(Mockito.any(FormResponse.class));
		Mockito.clearInvocations();
	}
	
	
	@Test
	public void testThrowsError() throws Exception {
		execute("throwsError");
		ArgumentCaptor<ErrorResponse> argument = ArgumentCaptor.forClass(ErrorResponse.class);
		Mockito.verify(rh)
			.accept(argument.capture());

		Assertions.assertEquals("Error123", argument.getValue().getData().get(ErrorResponse.MESSAGE_KEY));
		Mockito.clearInvocations();
	}
	


}
