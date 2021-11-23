package org.finos.springbot.symphony.controller;

import static org.mockito.Mockito.atMost;

import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.finos.springbot.entityjson.EntityJson;
import org.finos.springbot.symphony.SymphonyMockConfiguration;
import org.finos.springbot.symphony.SymphonyWorkflowConfig;
import org.finos.springbot.symphony.content.HashTag;
import org.finos.springbot.symphony.content.SymphonyUser;
import org.finos.springbot.symphony.form.ElementsHandler;
import org.finos.springbot.symphony.json.EntityJsonConverter;
import org.finos.springbot.symphony.messages.PresentationMLHandler;
import org.finos.springbot.tests.controller.AbstractHandlerMappingTest;
import org.finos.springbot.tests.controller.OurController;
import org.finos.springbot.workflow.actions.Action;
import org.finos.springbot.workflow.actions.SimpleMessageAction;
import org.finos.springbot.workflow.annotations.ChatRequest;
import org.finos.springbot.workflow.content.Message;
import org.finos.springbot.workflow.content.User;
import org.finos.springbot.workflow.form.ButtonList;
import org.finos.springbot.workflow.java.mapping.ChatMapping;
import org.finos.springbot.workflow.java.mapping.ChatRequestChatHandlerMapping;
import org.finos.springbot.workflow.response.WorkResponse;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.util.StreamUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.symphony.api.agent.MessagesApi;
import com.symphony.api.model.StreamType;
import com.symphony.api.model.V4Event;
import com.symphony.api.model.V4Initiator;
import com.symphony.api.model.V4Message;
import com.symphony.api.model.V4MessageSent;
import com.symphony.api.model.V4Payload;
import com.symphony.api.model.V4Stream;
import com.symphony.api.model.V4SymphonyElementsAction;
import com.symphony.api.model.V4User;


@SpringBootTest(classes = {
		SymphonyMockConfiguration.class, 
		SymphonyWorkflowConfig.class,
})
public class SymphonyHandlerMappingTest extends AbstractHandlerMappingTest {
	
	@Autowired
	ChatRequestChatHandlerMapping hm;
	
	@Autowired
	PresentationMLHandler mc;
	
	@Autowired
	ElementsHandler eh;
	
	@Autowired
	EntityJsonConverter ejc;
	
	@MockBean
	MessagesApi messagesApi;
	
	@Autowired
	OurController oc;
	
	ArgumentCaptor<String> msg;
	ArgumentCaptor<String> data;
	ArgumentCaptor<Object> att;

	@Override
	protected List<ChatMapping<ChatRequest>> getMappingsFor(Message m) throws Exception {
		EntityJson jsonObjects = new EntityJson();
		Action a = new SimpleMessageAction(null, null, m, jsonObjects);
		return hm.getHandlers(a);
	}
	

	@Override
	protected void execute(String s) throws Exception {
		oc.lastArguments = null;
		oc.lastMethod = null;
		s = s.replace("@gaurav","<span class=\"entity\" data-entity-id=\"1\">@gaurav</span>");
		
		Mockito.clearAllCaches();
		
		EntityJson jsonObjects = new EntityJson();
		jsonObjects.put("1", new SymphonyUser(123l, "gaurav", "gaurav@example.com"));
		jsonObjects.put("2", new HashTag("SomeTopic"));
		String dataStr = ejc.writeValue(jsonObjects);
		
		V4Event event = new V4Event()
			.payload(new V4Payload()
				.messageSent(new V4MessageSent()
						.message(new V4Message()
							.user(new V4User()
								.displayName(ROB_NAME)
								.email(ROB_EXAMPLE_EMAIL)
								.userId(ROB_EXAMPLE_ID))
							.stream(new V4Stream()
								.streamType(StreamType.TypeEnum.ROOM.getValue())
								.streamId(CHAT_ID))
							.message("<messageML>/"+s+"</messageML>")
							.data(dataStr))));
		
		msg = ArgumentCaptor.forClass(String.class);
		data = ArgumentCaptor.forClass(String.class);
		att = ArgumentCaptor.forClass(Object.class);		
		
		mc.accept(event);
		
		Mockito.verify(messagesApi, atMost(1)).v4StreamSidMessageCreatePost(
				Mockito.nullable(String.class), 
				Mockito.matches(CHAT_ID),
				msg.capture(),
				data.capture(),
				Mockito.isNull(), 
				att.capture(), 
				Mockito.isNull(), 
				Mockito.isNull());

	}

	@Override
	protected String getMessageData() {
		return data.getValue();
	}

	@Override
	protected String getMessageContent() {
		return msg.getValue();
	}

	@Override
	protected void pressButton(String s) {
		oc.lastArguments = null;
		oc.lastMethod = null;
		
		Mockito.clearAllCaches();
		
		Map<String, Object> values = new HashMap<>();
		values.put("action", s);
		
		V4Event event = new V4Event()
			.initiator(new V4Initiator().user(new V4User()
					.displayName(ROB_NAME)
					.email(ROB_EXAMPLE_EMAIL)
					.userId(ROB_EXAMPLE_ID)))
			.payload(new V4Payload()
				.symphonyElementsAction(new V4SymphonyElementsAction()
						.formValues(values)
						.stream(new V4Stream()
								.streamType(StreamType.TypeEnum.ROOM.getValue())
								.streamId(CHAT_ID))));
		
		msg = ArgumentCaptor.forClass(String.class);
		data = ArgumentCaptor.forClass(String.class);
		att = ArgumentCaptor.forClass(Object.class);		
		
		eh.accept(event);
		
		Mockito.verify(messagesApi, atMost(1)).v4StreamSidMessageCreatePost(
				Mockito.nullable(String.class), 
				Mockito.matches(CHAT_ID),
				msg.capture(),
				data.capture(),
				Mockito.isNull(), 
				att.capture(), 
				Mockito.isNull(), 
				Mockito.isNull());
	}


	@Override
	protected void assertHelpResponse() throws Exception {
		String msg = getMessageContent();
		String data = getMessageData();
		
		JsonNode node = new ObjectMapper().readTree(data);
		System.out.println(msg);
		System.out.println(data);
		
		
		Assertions.assertEquals(14, node.get(WorkResponse.OBJECT_KEY).get("commands").size());
		
		Assertions.assertTrue(data.contains(" {\n"
				+ "      \"type\" : \"org.finos.springbot.workflow.help.commandDescription\",\n"
				+ "      \"version\" : \"1.0\",\n"
				+ "      \"description\" : \"Display this help page\",\n"
				+ "      \"examples\" : [ \"help\" ]\n"
				+ "    }"));
		
		Assertions.assertTrue(msg.contains("Description"));
	}
	

	@Test
	public void testAttachmentResponse() throws Exception {
		execute("attachment");
		FileDataBodyPart fdbp = (FileDataBodyPart) att.getValue();
		String contents = StreamUtils.copyToString(
			new FileInputStream((File) fdbp.getEntity()), 
			Charset.defaultCharset());
		

		Assertions.assertEquals("payload", contents);
	}
	
	@Test
	public void testHashtagMapping() throws Exception {
		execute("add @gaurav to <span class=\"entity\" data-entity-id=\"2\">#SomeTopic</span>");
		Assertions.assertEquals("addUserToTopic", oc.lastMethod);
		Assertions.assertEquals(2,  oc.lastArguments.size());
		Object firstArgument = oc.lastArguments.get(0);
		Assertions.assertTrue(User.class.isAssignableFrom(firstArgument.getClass()));
		Assertions.assertEquals("gaurav", ((User)firstArgument).getName());
		
		Object secondArgument = oc.lastArguments.get(1);
		Assertions.assertTrue(HashTag.class.isAssignableFrom(secondArgument.getClass()));
		Assertions.assertEquals("SomeTopic", ((HashTag)secondArgument).getName());
	}
	

	@Override
	protected void assertNoButtons() {
		try {
			String data = getMessageData();
			JsonNode node = new ObjectMapper().readTree(data);

			JsonNode buttons = node.get(ButtonList.KEY).get("contents");
			Assertions.assertTrue(buttons.size() == 0);
			Assertions.assertFalse(getMessageContent().contains("<form"));
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}

	}

}
