package org.finos.symphony.toolkit.workflow;

import com.symphony.api.id.SymphonyIdentity;
import com.symphony.api.model.*;
import com.symphony.api.pod.UsersApi;
import org.finos.symphony.toolkit.workflow.sources.symphony.handlers.AttachmentHandler;
import org.finos.symphony.toolkit.workflow.sources.symphony.handlers.EntityJsonConverter;
import org.finos.symphony.toolkit.workflow.sources.symphony.handlers.SymphonyResponseHandler;
import org.finos.symphony.toolkit.workflow.sources.symphony.handlers.freemarker.FreemarkerFormMessageMLConverter;
import org.finos.symphony.toolkit.workflow.sources.symphony.messages.*;
import org.finos.symphony.toolkit.workflow.sources.symphony.room.SymphonyRooms;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ResourceLoader;

import java.util.Arrays;
import java.util.List;

public class TestPresentationMLHandler extends AbstractMockSymphonyTest {

	@Autowired
	Workflow wf;
	
	@Autowired
	SymphonyIdentity identity;
	
	PresentationMLHandler handler;
	
	@Autowired
	UsersApi usersApi;
	
	@MockBean
	SymphonyRooms symphonyRooms;
	
	@MockBean
	AttachmentHandler ah;
	
	@Autowired
	CommandPerformer cp;

	@Autowired
	ResourceLoader rl;
	
	@Autowired
	FreemarkerFormMessageMLConverter fmc;
	
	@BeforeEach
	public void setup() {
		SimpleMessageParser smp = new SimpleMessageParser();
		EntityJsonConverter ejc = new EntityJsonConverter(wf);
		List<SimpleMessageConsumer> consumers = Arrays.asList(new HelpMessageConsumer(), new MethodCallMessageConsumer(cp));
		SymphonyResponseHandler srh = new SymphonyResponseHandler(messagesApi, fmc, ejc, symphonyRooms, ah);
		handler = new PresentationMLHandler(wf, identity, usersApi, smp, ejc, consumers, srh, symphonyRooms);
	}
	
	@Test
	public void testHelp() {
		
		V4Event e = new V4Event()
			.payload(new V4Payload()
				.messageSent(new V4MessageSent()
					.message(new V4Message()
						.message("<div data-format=\"PresentationML\"><p>/help</p></div>")
						.user(new V4User().email("rob@example.com"))
							.stream(new V4Stream()
								.streamId("abc123")
								.streamType("ROOM")
											))));
		
		handler.accept(e);
		Mockito.verify(messagesApi).v4StreamSidMessageCreatePost(
				Mockito.isNull(), Mockito.isNull(), 
				Mockito.argThat(s -> 
					s.contains("<button name=\"add\" type=\"action\">add</button>")
					&& s.contains("<b> /remove</b>")), 
				Mockito.any(), Mockito.isNull(), Mockito.isNull(), Mockito.isNull(), Mockito.isNull());
	
	}
	
	@Test
	public void testMethodCall() {
		
		V4Event e = new V4Event()
				.payload(new V4Payload()
					.messageSent(new V4MessageSent()
						.message(new V4Message()
							.message("<div data-format=\"PresentationML\"><p>/remove 1</p></div>")
							.user(new V4User().email("rob@example.com"))
								.stream(new V4Stream()
									.streamId("abc123")
									.streamType("ROOM")
												))));
		
		handler.accept(e);
		
		// make sure we get back some json and some message ml
		Mockito.verify(messagesApi).v4StreamSidMessageCreatePost(
				Mockito.isNull(), Mockito.isNull(), 
				Mockito.anyString(), 
				Mockito.anyString(), Mockito.isNull(), Mockito.isNull(), Mockito.isNull(), Mockito.isNull());
	}

	@Test
	public void testMethodCallForThrowable() {

		V4Event e = new V4Event()
				.payload(new V4Payload()
						.messageSent(new V4MessageSent()
								.message(new V4Message()
										.message("<div data-format=\"PresentationML\"><p>/throwable 1</p></div>")
										.user(new V4User().email("rob@example.com"))
										.stream(new V4Stream()
												.streamId("abc123")
												.streamType("ROOM")
										))));

		handler.accept(e);

		// make sure we get back some json and some message ml
		Mockito.verify(messagesApi).v4StreamSidMessageCreatePost(
				Mockito.isNull(), Mockito.isNull(),
				Mockito.anyString(),
				Mockito.anyString(), Mockito.isNull(), Mockito.isNull(), Mockito.isNull(), Mockito.isNull());
	}
	
}
