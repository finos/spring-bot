package org.finos.symphony.toolkit.stream.cluster;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;

import java.util.Collections;

import org.finos.symphony.toolkit.stream.Participant;
import org.finos.symphony.toolkit.stream.cluster.messages.ClusterMessage;
import org.finos.symphony.toolkit.stream.cluster.messages.SuppressionMessage;
import org.finos.symphony.toolkit.stream.web.HttpMulticaster;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.matching.RequestPatternBuilder;

public class TestTransport {
	
	Participant me = new Participant("http://localhost:9998/me");
	Participant you = new Participant("http://localhost:9999/you");
	public WireMockServer wireMockRule = new WireMockServer(9999);
	
	@BeforeEach
	public void setupWireMock() throws JsonProcessingException {
		wireMockRule.stubFor(post(urlEqualTo("/you"))
			.willReturn(aResponse()
				.withHeader("Content-Type", "application/json")
				.withBody("")));
		wireMockRule.start();
	}
	
	
	@Test
	public void testHttpTransport() {
		HttpMulticaster hm = new HttpMulticaster(me);
		ClusterMessage cm = new SuppressionMessage("test", me);
		hm.sendAsyncMessage(me, Collections.singletonList(you),cm);
		wireMockRule.verify(1, RequestPatternBuilder.allRequests());
	}
	
	@AfterEach
	public void tearDown() {
		wireMockRule.stop();
	}

}
