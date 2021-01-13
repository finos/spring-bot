package org.finos.symphony.toolkit.koreai.bot;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

import java.util.ArrayList;
import java.util.Collections;

import org.finos.symphony.toolkit.json.EntityJson;
import org.finos.symphony.toolkit.stream.handler.SymphonyStreamHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.symphony.api.model.V4Event;
import com.symphony.api.model.V4Initiator;
import com.symphony.api.model.V4Message;
import com.symphony.api.model.V4MessageSent;
import com.symphony.api.model.V4Payload;
import com.symphony.api.model.V4Stream;
import com.symphony.api.model.V4SymphonyElementsAction;
import com.symphony.api.model.V4User;
import com.symphony.user.Mention;
import com.symphony.user.UserId;

public class BotIT extends AbstractBotIT {


	@BeforeEach
	public void reset() {
		wireMockRule.resetRequests();
	}
	
	/**
	 * When the user presses a button, we return a response.
	 */
	@Test
	public void testPressButton() {
		V4Event in = new V4Event()
				.initiator(new V4Initiator()
						.user(new V4User().email("rob@example.com").displayName("Rob Example").userId(2438923l)))
				.payload(new V4Payload().symphonyElementsAction(new V4SymphonyElementsAction().formId("koreai-choice")
						.formValues(Collections.singletonMap("action", "some button"))
						.stream(new V4Stream().streamId("ABC123"))));

		getPublicBot().sendToConsumer(in);
		littleSleep();
		wireMockRule.verify(1, WireMock.postRequestedFor(urlPathMatching("/agent/v4/stream/ABC123/message/create")));
	}

	private SymphonyStreamHandler getPublicBot() {
		return ssh.get(0);
	}

	@Test
	public void testSendMessageIM() {
		V4Event in = new V4Event()
				.initiator(new V4Initiator()
						.user(new V4User().email("rob@example.com").displayName("Rob Example").userId(2438923l)))
				.payload(new V4Payload().messageSent(new V4MessageSent().message(
						new V4Message().message("<div>hello</div>").data("{}").stream(new V4Stream().streamId("ABC123").streamType("IM")))));

		getPublicBot().sendToConsumer(in);
		littleSleep();
		wireMockRule.verify(1, WireMock.postRequestedFor(urlPathMatching("/agent/v4/stream/ABC123/message/create")));
	}
	
	@Test
	public void testSendMessageRoomWithoutSlash() {
		V4Event in = new V4Event()
				.initiator(new V4Initiator()
						.user(new V4User().email("rob@example.com").displayName("Rob Example").userId(2438923l)))
				.payload(new V4Payload().messageSent(new V4MessageSent().message(
						new V4Message().message("<div>hello</div>").data("{}").stream(new V4Stream()
								.streamId("ABC123").streamType("ROOM")))));

		getPublicBot().sendToConsumer(in);
		littleSleep();
		
		// we shouldn't see a message sent
		wireMockRule.verify(0, WireMock.postRequestedFor(urlPathMatching("/agent/v4/stream/ABC123/message/create")));
	}
	
	@Test
	public void testSendMessageRoomWithSlash() {
		V4Event in = new V4Event()
				.initiator(new V4Initiator()
						.user(new V4User().email("rob@example.com").displayName("Rob Example").userId(2438923l)))
				.payload(new V4Payload().messageSent(new V4MessageSent().message(
						new V4Message().message("<div>/hello</div>").data("{}").stream(new V4Stream()
								.streamId("ABC123").streamType("ROOM")))));

		getPublicBot().sendToConsumer(in);
		littleSleep();
		wireMockRule.verify(1, 
			WireMock.postRequestedFor(urlPathMatching("/kore"))
				.withRequestBody(matching(".*\"message\":\\{\"text\":\"hello\"\\}.*")));
	}
	
	@Test
	public void testSendMessageRoomWithMention() throws JsonProcessingException {
		Mention m = new Mention();
		m.setId(new ArrayList<>());
		m.getId().add(new UserId("1234"));
		EntityJson ej = new EntityJson();
		ej.put("m1", m);
		String data = symphonyObjectMapper.writeValueAsString(ej);
		
		V4Event in = new V4Event()
				.initiator(new V4Initiator()
						.user(new V4User().email("rob@example.com").displayName("Rob Example").userId(2438923l)))
				.payload(new V4Payload().messageSent(new V4MessageSent()
						.message(new V4Message().message("<div>hello</div>")
								.stream(new V4Stream().streamId("ABC123").streamType("ROOM"))
								.data(data))));
					

		getPublicBot().sendToConsumer(in);
		littleSleep();
		wireMockRule.verify(1, WireMock.postRequestedFor(urlPathMatching("/agent/v4/stream/ABC123/message/create")));
	}
	
	private SymphonyStreamHandler getPrivateBot() {
		return ssh.get(1);
	}

	@Test
	public void testSendMessageIMPrivateBot() {
		V4Event in = new V4Event()
				.initiator(new V4Initiator()
						.user(new V4User().email("rob@example.com").displayName("Rob Example").userId(2438923l)))
				.payload(new V4Payload().messageSent(new V4MessageSent().message(
						new V4Message().message("<div>hello</div>").data("{}").stream(new V4Stream().streamId("ABC123").streamType("IM")))));

		getPrivateBot().sendToConsumer(in);
		littleSleep();
		wireMockRule.verify(1, WireMock.postRequestedFor(urlPathMatching("/kore2")));
		wireMockRule.verify(1, WireMock.postRequestedFor(urlPathMatching("/agent/v4/stream/ABC123/message/create")));
	}
	
	@Test
	public void testSendMessageRoomWithSlashPrivateBot() {
		// the direct bot won't talk in rooms
		V4Event in = new V4Event()
				.initiator(new V4Initiator()
						.user(new V4User().email("rob@example.com").displayName("Rob Example").userId(2438923l)))
				.payload(new V4Payload().messageSent(new V4MessageSent().message(
						new V4Message().message("<div>/hello</div>").data("{}").stream(new V4Stream()
								.streamId("ABC123").streamType("ROOM")))));

		getPrivateBot().sendToConsumer(in);
		littleSleep();
		wireMockRule.verify(0, WireMock.postRequestedFor(urlPathMatching("/kore2")));
	}
}
