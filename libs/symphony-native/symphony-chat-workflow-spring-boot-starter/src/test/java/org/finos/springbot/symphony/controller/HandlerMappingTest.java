package org.finos.springbot.symphony.controller;

import static org.mockito.Mockito.atMost;

import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.Charset;
import java.util.List;

import org.finos.springbot.tests.controller.AbstractHandlerMappingTest;
import org.finos.springbot.tests.controller.OurController;
import org.finos.springbot.workflow.actions.Action;
import org.finos.springbot.workflow.actions.SimpleMessageAction;
import org.finos.springbot.workflow.annotations.ChatRequest;
import org.finos.springbot.workflow.content.Message;
import org.finos.springbot.workflow.content.User;
import org.finos.springbot.workflow.java.mapping.ChatMapping;
import org.finos.springbot.workflow.java.mapping.ChatRequestChatHandlerMapping;
import org.finos.symphony.toolkit.json.EntityJson;
import org.finos.symphony.toolkit.workflow.SymphonyMockConfiguration;
import org.finos.symphony.toolkit.workflow.sources.symphony.SymphonyWorkflowConfig;
import org.finos.symphony.toolkit.workflow.sources.symphony.content.HashTag;
import org.finos.symphony.toolkit.workflow.sources.symphony.content.SymphonyUser;
import org.finos.symphony.toolkit.workflow.sources.symphony.json.EntityJsonConverter;
import org.finos.symphony.toolkit.workflow.sources.symphony.messages.PresentationMLHandler;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.util.StreamUtils;

import com.symphony.api.agent.MessagesApi;
import com.symphony.api.model.StreamType;
import com.symphony.api.model.V4Event;
import com.symphony.api.model.V4Message;
import com.symphony.api.model.V4MessageSent;
import com.symphony.api.model.V4Payload;
import com.symphony.api.model.V4Stream;
import com.symphony.api.model.V4User;


@SpringBootTest(classes = {
		SymphonyMockConfiguration.class, 
		SymphonyWorkflowConfig.class,
})
public class HandlerMappingTest extends AbstractHandlerMappingTest {
	
	@Autowired
	ChatRequestChatHandlerMapping hm;
	
	@Autowired
	PresentationMLHandler mc;
	
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
		// TODO Auto-generated method stub
		
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
	
}
