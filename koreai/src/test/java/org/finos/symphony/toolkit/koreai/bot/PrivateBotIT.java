package org.finos.symphony.toolkit.koreai.bot;

import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;

import org.finos.symphony.toolkit.stream.handler.SymphonyStreamHandler;
import org.junit.jupiter.api.Test;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.symphony.api.model.V4Event;
import com.symphony.api.model.V4Initiator;
import com.symphony.api.model.V4Message;
import com.symphony.api.model.V4MessageSent;
import com.symphony.api.model.V4Payload;
import com.symphony.api.model.V4Stream;
import com.symphony.api.model.V4User;

public class PrivateBotIT extends AbstractBotIT {


	private SymphonyStreamHandler getPrivateBot() {
		return ssh.get(1);
	}

	@Test
	public void testSendMessageIM() {
		V4Event in = new V4Event()
				.initiator(new V4Initiator()
						.user(new V4User().email("rob@example.com").displayName("Rob Example").userId(2438923l)))
				.payload(new V4Payload().messageSent(new V4MessageSent().message(
						new V4Message().message("<div>hello</div>").data("{}").stream(new V4Stream().streamId("ABC123").streamType("IM")))));

		getPrivateBot().sendToConsumer(in);
		wireMockRule.verify(1, WireMock.postRequestedFor(urlPathMatching("/kore2")));
		wireMockRule.verify(1, WireMock.postRequestedFor(urlPathMatching("/agent/v4/stream/ABC123/message/create")));
	}
	
	@Test
	public void testSendMessageRoomWithSlash() {
		// the direct bot won't talk in rooms
		V4Event in = new V4Event()
				.initiator(new V4Initiator()
						.user(new V4User().email("rob@example.com").displayName("Rob Example").userId(2438923l)))
				.payload(new V4Payload().messageSent(new V4MessageSent().message(
						new V4Message().message("<div>/hello</div>").data("{}").stream(new V4Stream()
								.streamId("ABC123").streamType("ROOM")))));

		getPrivateBot().sendToConsumer(in);
		wireMockRule.verify(0, WireMock.postRequestedFor(urlPathMatching("/kore2")));
	}
	
}
