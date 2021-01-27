package org.finos.symphony.toolkit.workflow;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.finos.symphony.toolkit.json.EntityJson;
import org.finos.symphony.toolkit.workflow.fixture.TestObjects;
import org.finos.symphony.toolkit.workflow.sources.symphony.elements.ElementsHandler;
import org.finos.symphony.toolkit.workflow.sources.symphony.elements.FormConverter;
import org.finos.symphony.toolkit.workflow.sources.symphony.elements.MethodCallElementsConsumer;
import org.finos.symphony.toolkit.workflow.sources.symphony.handlers.AttachmentHandler;
import org.finos.symphony.toolkit.workflow.sources.symphony.handlers.EntityJsonConverter;
import org.finos.symphony.toolkit.workflow.sources.symphony.handlers.FormMessageMLConverter;
import org.finos.symphony.toolkit.workflow.sources.symphony.handlers.SymphonyResponseHandler;
import org.finos.symphony.toolkit.workflow.sources.symphony.handlers.freemarker.FieldConverter;
import org.finos.symphony.toolkit.workflow.sources.symphony.handlers.freemarker.FreemarkerFormMessageMLConverter;
import org.finos.symphony.toolkit.workflow.sources.symphony.room.SymphonyRooms;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ResourceLoader;
import org.springframework.validation.Validator;

import com.symphony.api.id.SymphonyIdentity;
import com.symphony.api.model.V4Event;
import com.symphony.api.model.V4Initiator;
import com.symphony.api.model.V4Message;
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

	@Autowired
	ResourceLoader rl;
	
	@Autowired
	CommandPerformer cp;
	
	@Autowired
	List<FieldConverter> fieldConverters;
	
	@BeforeEach
	public void setup() {
		ejc = new EntityJsonConverter(wf);
		FormConverter fc = new FormConverter(symphonyRooms);
		FormMessageMLConverter fmc = new FreemarkerFormMessageMLConverter(rl, fieldConverters);
		MethodCallElementsConsumer mcec = new MethodCallElementsConsumer(cp);
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
				Mockito.isNull(), 
				Mockito.isNull(), 
				Mockito.any(), 
				Mockito.any(), Mockito.isNull(), Mockito.isNull(), Mockito.isNull(), Mockito.isNull());
			
	}
	
}
