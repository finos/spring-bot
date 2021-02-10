package org.finos.symphony.toolkit.stream.cluster;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;

import java.util.ArrayList;
import java.util.List;

import org.finos.symphony.toolkit.stream.Participant;
import org.finos.symphony.toolkit.stream.cluster.messages.ClusterMessage;
import org.finos.symphony.toolkit.stream.cluster.messages.SuppressionMessage;
import org.finos.symphony.toolkit.stream.cluster.messages.VoteRequest;
import org.finos.symphony.toolkit.stream.cluster.transport.HttpMulticaster;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;

public class TestTransport {
	
	Participant me = new Participant("http://localhost:9998/me");
	Participant you = new Participant("http://localhost:9999/you");
	VoteRequest vote = new VoteRequest(0, you);

	public WireMockServer wireMockRule = new WireMockServer(9999);
	
	@BeforeEach
	public void setupWireMock() throws JsonProcessingException {
		String json = new ObjectMapper().writeValueAsString(vote);
		wireMockRule.stubFor(post(urlEqualTo("/you"))
			.willReturn(aResponse()
				.withHeader("Content-Type", "application/json")
				.withBody(json)));
		wireMockRule.start();
	}
	
	
	@Test
	public void testHttpTransport() {
		HttpMulticaster hm = new HttpMulticaster(me);
		hm.accept(you);
		ClusterMessage cm = new SuppressionMessage(me, 5);
		List<ClusterMessage> messages = new ArrayList<ClusterMessage>();
		hm.sendAsyncMessage(me, cm, e -> messages.add(e));
		while (messages.size() == 0) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e1) {
			}
		}
		
		Assertions.assertEquals(vote, messages.get(0));
	}

}
