package com.github.deutschebank.symphony.workflow;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ResourceLoader;

import com.github.deutschebank.symphony.workflow.sources.symphony.handlers.AttachmentHandler;
import com.github.deutschebank.symphony.workflow.sources.symphony.handlers.EntityJsonConverter;
import com.github.deutschebank.symphony.workflow.sources.symphony.handlers.FormMessageMLConverter;
import com.github.deutschebank.symphony.workflow.sources.symphony.handlers.FreemarkerFormMessageMLConverter;
import com.github.deutschebank.symphony.workflow.sources.symphony.handlers.SymphonyResponseHandler;
import com.github.deutschebank.symphony.workflow.sources.symphony.messages.HelpMessageConsumer;
import com.github.deutschebank.symphony.workflow.sources.symphony.messages.MethodCallMessageConsumer;
import com.github.deutschebank.symphony.workflow.sources.symphony.messages.PresentationMLHandler;
import com.github.deutschebank.symphony.workflow.sources.symphony.messages.SimpleMessageConsumer;
import com.github.deutschebank.symphony.workflow.sources.symphony.messages.SimpleMessageParser;
import com.github.deutschebank.symphony.workflow.sources.symphony.room.SymphonyRooms;
import com.symphony.api.id.SymphonyIdentity;
import com.symphony.api.model.V4Event;
import com.symphony.api.model.V4Message;
import com.symphony.api.model.V4MessageSent;
import com.symphony.api.model.V4Payload;
import com.symphony.api.model.V4Stream;
import com.symphony.api.model.V4User;
import com.symphony.api.pod.UsersApi;

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
	ResourceLoader rl;
	
	@Before
	public void setup() {
		SimpleMessageParser smp = new SimpleMessageParser();
		EntityJsonConverter ejc = new EntityJsonConverter(wf);
		FormMessageMLConverter fmc = new FreemarkerFormMessageMLConverter(symphonyRooms, rl);
		List<SimpleMessageConsumer> consumers = Arrays.asList(new HelpMessageConsumer(), new MethodCallMessageConsumer());
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
					s.contains("<td><button name=\"add\" type=\"action\">add</button></td><td><b> /add</b></td><td> Add another test object</td>")
					&& s.contains("<td></td><td><b> /remove</b></td><td> removes item by number. e.g. /remove 4</td>")), 
				Mockito.isNull(), Mockito.isNull(), Mockito.isNull(), Mockito.isNull(), Mockito.isNull());
	
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
	
}
