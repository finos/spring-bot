package org.finos.springbot.symphony.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.symphony.bdk.core.service.message.MessageService;
import com.symphony.bdk.core.service.message.model.Attachment;
import com.symphony.bdk.gen.api.model.*;
import com.symphony.bdk.spring.events.RealTimeEvent;
import org.finos.springbot.entityjson.EntityJson;
import org.finos.springbot.symphony.SymphonyMockConfiguration;
import org.finos.springbot.symphony.SymphonyWorkflowConfig;
import org.finos.springbot.symphony.content.HashTag;
import org.finos.springbot.symphony.content.SymphonyUser;
import org.finos.springbot.symphony.form.ElementsHandler;
import org.finos.springbot.symphony.messages.PresentationMLHandler;
import org.finos.springbot.tests.controller.AbstractHandlerMappingTest;
import org.finos.springbot.tests.controller.OurController;
import org.finos.springbot.workflow.actions.Action;
import org.finos.springbot.workflow.actions.SimpleMessageAction;
import org.finos.springbot.workflow.annotations.ChatRequest;
import org.finos.springbot.workflow.content.Message;
import org.finos.springbot.workflow.content.User;
import org.finos.springbot.workflow.data.EntityJsonConverter;
import org.finos.springbot.workflow.form.ButtonList;
import org.finos.springbot.workflow.java.mapping.ChatMapping;
import org.finos.springbot.workflow.java.mapping.ChatRequestChatHandlerMapping;
import org.finos.springbot.workflow.response.WorkResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.StreamUtils;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.atMost;


@SpringBootTest(classes = {
		SymphonyMockConfiguration.class, 
		SymphonyWorkflowConfig.class
})
@ActiveProfiles(value = "symphony")
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
	MessageService messagesApi;
	
	@Autowired
	OurController oc;
	
	ArgumentCaptor<com.symphony.bdk.core.service.message.model.Message> msg;

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
		
		V4MessageSent v4ms = new V4MessageSent()
						.message(new V4Message()
							.user(new V4User()
								.username(ROB_NAME)
								.displayName(ROB_NAME)
								.email(ROB_EXAMPLE_EMAIL)
								.userId(ROB_EXAMPLE_ID))
							.stream(new V4Stream()
								.streamType(StreamType.TypeEnum.ROOM.getValue())
								.streamId(CHAT_ID))
							.message("<messageML>/"+s+"</messageML>")
							.data(dataStr));
		
		msg = ArgumentCaptor.forClass(com.symphony.bdk.core.service.message.model.Message.class);	
		
		RealTimeEvent<V4MessageSent> rte = new RealTimeEvent<>(null, v4ms);
		
		mc.onApplicationEvent(rte);
		
		Mockito.verify(messagesApi, atMost(1)).send(Mockito.matches(CHAT_ID), msg.capture());
	}

	@Override
	protected String getMessageData() {
		return msg.getValue().getData();
	}

	@Override
	protected String getMessageContent() {
		return msg.getValue().getContent();
	}
	
	protected List<Attachment> getAttachments() {
		return msg.getValue().getAttachments();
	}

	@Override
	protected void pressButton(String s, Map<String, Object> values) {
		oc.lastArguments = null;
		oc.lastMethod = null;
		
		Mockito.clearAllCaches();
		
		values.put("action", s);
		
		V4Initiator initiator = new V4Initiator().user(new V4User()
					.displayName(ROB_NAME)
					.email(ROB_EXAMPLE_EMAIL)
					.userId(ROB_EXAMPLE_ID));
		
		V4SymphonyElementsAction action = new V4SymphonyElementsAction()
						.formValues(values)
						.formId((String) values.remove("form"))
						.stream(new V4Stream()
								.streamType(StreamType.TypeEnum.ROOM.getValue())
								.streamId(CHAT_ID));
		
		RealTimeEvent<V4SymphonyElementsAction> event = new RealTimeEvent<V4SymphonyElementsAction>(initiator, action);
		
		msg = ArgumentCaptor.forClass(com.symphony.bdk.core.service.message.model.Message.class);	
		
		eh.accept(event);
		
		Mockito.verify(messagesApi, atMost(1)).send(
				Mockito.matches(CHAT_ID),
				msg.capture());
		
	}


	@Override
	protected void assertHelpResponse() throws Exception {
		String msg = getMessageContent();
		String data = getMessageData();
		
		JsonNode node = new ObjectMapper().readTree(data);
		System.out.println(msg);
		System.out.println(data);
		
		
		Assertions.assertEquals(15, node.get(WorkResponse.OBJECT_KEY).get("commands").size());

		String desc = null;
		for(JsonNode commandNode : node.get(WorkResponse.OBJECT_KEY).get("commands")) {
			desc = commandNode.get("description").asText();

			//Button suppressed for commands with parameters
			if("Add User To Topic".equalsIgnoreCase(desc)
					|| "Ban Word".equalsIgnoreCase(desc)
					|| "Do List".equalsIgnoreCase(desc)
					|| "Process1".equalsIgnoreCase(desc)
					|| "Process2".equalsIgnoreCase(desc)
					|| "Remove User From Room".equalsIgnoreCase(desc)
					|| "User Details".equalsIgnoreCase(desc)
					|| "User Details2".equalsIgnoreCase(desc)) {
				Assertions.assertFalse(commandNode.get("button").asBoolean());

			} else if("Attachment".equalsIgnoreCase(desc) //Button enabled for commands without parameters
					|| "Do Command".equalsIgnoreCase(desc)
					|| "Do blah with a form".equalsIgnoreCase(desc)
					|| "Form2".equalsIgnoreCase(desc)
					|| "Throws Error".equalsIgnoreCase(desc)) {
				Assertions.assertTrue(commandNode.get("button").asBoolean());

			} else if("Display this help page".equalsIgnoreCase(desc)) { //help test
				Assertions.assertTrue(commandNode.get("button").asBoolean());
				Assertions.assertTrue(commandNode.get("type").asText()
						.equalsIgnoreCase("org.finos.springbot.workflow.help.commandDescription")
						&& commandNode.get("version").asText().equalsIgnoreCase("1.0"));
			}
		}

		Assertions.assertTrue(msg.contains("Description"));
	}
	

	@Test
	public void testAttachmentResponse() throws Exception {
		execute("attachment");
		List<Attachment> attachments = getAttachments();
		Assertions.assertEquals(1, attachments.size());
		Attachment first = attachments.get(0);
		String contents = StreamUtils.copyToString(first.getContent(), StandardCharsets.UTF_8);
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


	@Override
	protected void assertThrowsResponse() {
		Assertions.assertTrue(getMessageData().contains("Error123"));
	}

}
