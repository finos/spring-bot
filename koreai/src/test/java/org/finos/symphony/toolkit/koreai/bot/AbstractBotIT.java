package org.finos.symphony.toolkit.koreai.bot;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;

import java.util.List;

import javax.inject.Named;

import org.finos.symphony.toolkit.koreai.KoreAIBot;
import org.finos.symphony.toolkit.koreai.spring.KoreAIConfig;
import org.finos.symphony.toolkit.stream.handler.ExceptionConsumer;
import org.finos.symphony.toolkit.stream.handler.SymphonyStreamHandler;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.StreamUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.matching.EqualToPattern;
import com.google.common.base.Charsets;

@ExtendWith(SpringExtension.class)

@ActiveProfiles({ "test" })
@SpringBootTest(classes = { KoreAIBot.class, KoreAIConfig.class })
public abstract class AbstractBotIT {

	static WireMockServer wireMockRule = new WireMockServer(9999);

	@Autowired
	ApplicationContext ctx;

	@Autowired
	@Named(KoreAIConfig.KORE_AI_BRIDGE_LIST_BEAN)
	protected List<SymphonyStreamHandler> ssh;

	@Autowired
	protected ObjectMapper symphonyObjectMapper;
	
	@MockBean
	ExceptionConsumer ec;

	@BeforeAll
	public static void setupWireMock() throws Exception {
		String response = StreamUtils.copyToString(BotIT.class.getResourceAsStream("ans1.json"), Charsets.UTF_8);
		wireMockRule
				.stubFor(post(urlEqualTo("/kore")).withHeader("Authorization", new EqualToPattern("Bearer some-jwt"))
						.willReturn(aResponse().withHeader("Content-Type", "application/json").withBody(response)));

		wireMockRule.stubFor(
				post(urlEqualTo("/kore2")).withHeader("Authorization", new EqualToPattern("Bearer some-other-jwt"))
						.willReturn(aResponse().withHeader("Content-Type", "application/json").withBody(response)));

		wireMockRule.stubFor(get(urlPathMatching("/pod/v1/user"))
				.willReturn(aResponse().withHeader("Content-Type", "application/json").withBody("{\"id\":1234}")));

		wireMockRule.stubFor(post(urlPathMatching("/login/pubkey/authenticate")).willReturn(
				aResponse().withHeader("Content-Type", "application/json").withBody("{\"token\": \"session123\"}")));

		wireMockRule.stubFor(post(urlPathMatching("/relay/pubkey/authenticate")).willReturn(
				aResponse().withHeader("Content-Type", "application/json").withBody("{\"token\": \"km123\"}")));

		wireMockRule.stubFor(post(urlPathMatching("/agent/v4/stream/someblx/message/create"))
				.willReturn(aResponse().withHeader("Content-Type", "application/json").withBody("{}")));

		wireMockRule.stubFor(post(urlPathMatching("/agent/v4/datafeed/create")).willReturn(
				aResponse().withHeader("Content-Type", "application/json").withBody("{\"id\": \"somedatafeedid\" }")));

		wireMockRule.stubFor(post(urlPathMatching("/agent/v4/datafeed/create")).willReturn(
				aResponse().withHeader("Content-Type", "application/json").withBody("{\"id\": \"somedatafeedid\" }")));

		wireMockRule.stubFor(get(urlPathMatching("/agent/v4/datafeed/somedatafeedid/read")).willReturn(
				aResponse().withHeader("Content-Type", "application/json").withFixedDelay(50000).withBody("{}")));

		wireMockRule.stubFor(post(urlPathMatching("/agent/v4/stream/ABC123/message/create"))
				.willReturn(aResponse().withHeader("Content-Type", "application/json").withBody("{}")));

		wireMockRule.start();

	}

	public void littleSleep() {
		try {
			Thread.sleep(400);
		} catch (InterruptedException e) {
		}
	}
	
	@AfterAll
	public static void close() {
		wireMockRule.shutdown();
	}

	public AbstractBotIT() {
		super();
	}

}