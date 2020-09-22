package com.github.deutschebank.symphony.workflow;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.validation.Validator;

import com.github.deutschebank.symphony.json.EntityJson;
import com.github.deutschebank.symphony.workflow.fixture.TestOb3;
import com.github.deutschebank.symphony.workflow.fixture.TestObject;
import com.github.deutschebank.symphony.workflow.fixture.TestObjects;
import com.github.deutschebank.symphony.workflow.sources.symphony.elements.ElementsHandler;
import com.github.deutschebank.symphony.workflow.sources.symphony.elements.FormConverter;
import com.github.deutschebank.symphony.workflow.sources.symphony.elements.MethodCallElementsConsumer;
import com.github.deutschebank.symphony.workflow.sources.symphony.handlers.AttachmentHandler;
import com.github.deutschebank.symphony.workflow.sources.symphony.handlers.EntityJsonConverter;
import com.github.deutschebank.symphony.workflow.sources.symphony.handlers.FormMessageMLConverter;
import com.github.deutschebank.symphony.workflow.sources.symphony.handlers.SymphonyResponseHandler;
import com.github.deutschebank.symphony.workflow.sources.symphony.messages.HelpMessageConsumer;
import com.github.deutschebank.symphony.workflow.sources.symphony.messages.MethodCallMessageConsumer;
import com.github.deutschebank.symphony.workflow.sources.symphony.messages.PresentationMLHandler;
import com.github.deutschebank.symphony.workflow.sources.symphony.messages.SimpleMessageConsumer;
import com.github.deutschebank.symphony.workflow.sources.symphony.messages.SimpleMessageParser;
import com.github.deutschebank.symphony.workflow.sources.symphony.room.SymphonyRooms;
import com.symphony.api.id.SymphonyIdentity;
import com.symphony.api.model.V4Event;
import com.symphony.api.model.V4Initiator;
import com.symphony.api.model.V4Message;
import com.symphony.api.model.V4MessageSent;
import com.symphony.api.model.V4Payload;
import com.symphony.api.model.V4Stream;
import com.symphony.api.model.V4SymphonyElementsAction;
import com.symphony.api.model.V4User;
import com.symphony.api.pod.UsersApi;

public class TestActionElementsHandler extends AbstractMockSymphonyTest {

	@Autowired
	Workflow wf;
	
	@Autowired
	SymphonyIdentity identity;
	
	ElementsHandler handler;
	
	@Autowired
	UsersApi usersApi;
	
	@MockBean
	SymphonyRooms symphonyRooms;
	
	@MockBean
	AttachmentHandler ah;
	
	@Autowired
	Validator v;
	
	EntityJsonConverter ejc;
	
	@Before
	public void setup() {
		ejc = new EntityJsonConverter(wf);
		FormConverter fc = new FormConverter(symphonyRooms);
		FormMessageMLConverter fmc = new FormMessageMLConverter(symphonyRooms);
		MethodCallElementsConsumer mcec = new MethodCallElementsConsumer();
		SymphonyResponseHandler srh = new SymphonyResponseHandler(messagesApi, fmc, ejc, symphonyRooms, ah);
		handler = new ElementsHandler(wf, messagesApi, ejc,  fc, Arrays.asList(mcec), srh, symphonyRooms, v);
	}
	
	@Test
	public void testPressButton() {
		// dummy the originating message
		Mockito.when(messagesApi.v1MessageIdGet(Mockito.isNull(), Mockito.isNull(), Mockito.anyString()))
			.thenAnswer(iom -> new V4Message().data(null));
		
		// simulate press of /show button on help
		Map<String, String> action = Collections.singletonMap("action", "show");
		String formId = TestObjects.class.getCanonicalName();
		V4Event e = new V4Event()
			.initiator(new V4Initiator()
				.user(new V4User()
					.email("rob@example.com")))
			.payload(new V4Payload()
				.symphonyElementsAction(new V4SymphonyElementsAction()
						.formId(formId)
						.formMessageId("originalmessage")
						.formValues(action)
						.stream(new V4Stream()
							.streamId("abc123")
							.streamType("ROOM")
											)));
		
		// ensure returns TestObjects
		handler.accept(e);
		Mockito.verify(messagesApi).v4StreamSidMessageCreatePost(
				Mockito.isNull(), Mockito.isNull(), 
				Mockito.anyString(),
				Mockito.argThat(s -> {
					EntityJson o = ejc.readValue(s);
					return o.get("workflow_001") instanceof TestObjects;
				}), 
				Mockito.isNull(), Mockito.isNull(), Mockito.isNull(), Mockito.isNull());
	
	}
	
	@Test
	public void testMethodCallWithoutParameter() {
		// dummy the originating message
		String originalJson = ejc.toWorkflowJson(new TestObjects());
		Mockito.when(messagesApi.v1MessageIdGet(Mockito.isNull(), Mockito.isNull(), Mockito.anyString()))
			.thenAnswer(iom -> new V4Message().data(originalJson));
				
		// simulate press of /add button on help
		Map<String, String> action = Collections.singletonMap("action", "add+1");
		String formId = TestObjects.class.getCanonicalName();
		V4Event e = new V4Event()
			.initiator(new V4Initiator()
				.user(new V4User()
					.email("rob@example.com")))
			.payload(new V4Payload()
				.symphonyElementsAction(new V4SymphonyElementsAction()
						.formId(formId)
						.formMessageId("originalmessage")
						.formValues(action)
						.stream(new V4Stream()
							.streamId("abc123")
							.streamType("ROOM")
											)));
				
		// should return a form for TestObject
		handler.accept(e);
		Mockito.verify(messagesApi).v4StreamSidMessageCreatePost(
				Mockito.isNull(), Mockito.isNull(), 
				Mockito.argThat(s ->  s.contains("<form id=\""+TestObject.class.getCanonicalName()+"\"")), 
				Mockito.isNull(), Mockito.isNull(), Mockito.isNull(), Mockito.isNull(), Mockito.isNull());
			
	}
	
}
