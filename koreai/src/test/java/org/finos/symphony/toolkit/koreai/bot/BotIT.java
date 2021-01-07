package org.finos.symphony.toolkit.koreai.bot;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Named;

import org.finos.symphony.toolkit.json.EntityJson;
import org.finos.symphony.toolkit.koreai.KoreAIBot;
import org.finos.symphony.toolkit.koreai.spring.KoreAIConfig;
import org.finos.symphony.toolkit.stream.handler.SymphonyStreamHandler;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StreamUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.matching.EqualToPattern;
import com.google.common.base.Charsets;
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

@RunWith(SpringRunner.class)
@ActiveProfiles({ "test" })
@SpringBootTest(classes = { KoreAIBot.class })
@TestPropertySource(properties = {
	"symphony.koreai.only-addressed=true",
	"symphony.stream.startImmediately=false"
})
public class BotIT {

	static WireMockServer wireMockRule = new WireMockServer(9999);

	
	@Autowired
	ApplicationContext ctx;
	
	@Autowired
	@Named(KoreAIConfig.KORE_AI_BRIDGE_LIST_BEAN)
	List<SymphonyStreamHandler> ssh;
	
	@Autowired
	ObjectMapper symphonyObjectMapper;

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

		ssh.get(0).sendToConsumer(in);
		wireMockRule.verify(1, WireMock.postRequestedFor(urlPathMatching("/agent/v4/stream/ABC123/message/create")));
	}

	@Test
	public void testSendMessageIM() {
		V4Event in = new V4Event()
				.initiator(new V4Initiator()
						.user(new V4User().email("rob@example.com").displayName("Rob Example").userId(2438923l)))
				.payload(new V4Payload().messageSent(new V4MessageSent().message(
						new V4Message().message("<div>hello</div>").data("{}").stream(new V4Stream().streamId("ABC123").streamType("IM")))));

		ssh.get(0).sendToConsumer(in);
		wireMockRule.verify(1, WireMock.postRequestedFor(urlPathMatching("/agent/v4/stream/ABC123/message/create")));
	}
	
	@Test
	public void testSendMessageRoom() throws JsonProcessingException {
		Mention m = new Mention();
		m.setId(new ArrayList<>());
		m.getId().add(new UserId("123"));
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
					

		ssh.get(0).sendToConsumer(in);
		wireMockRule.verify(1, WireMock.postRequestedFor(urlPathMatching("/agent/v4/stream/ABC123/message/create")));
	}

	@Before
	public void reset() {
		wireMockRule.resetRequests();
	}
	
	@BeforeClass
	public static void setupWireMock() throws Exception {
		String response = StreamUtils.copyToString(BotIT.class.getResourceAsStream("ans1.json"), Charsets.UTF_8);
		wireMockRule
				.stubFor(post(urlEqualTo("/kore")).withHeader("Authorization", new EqualToPattern("Bearer some-jwt"))
						// .withRequestBody(new
						// EqualToPattern("{\"entity\":{\"to\":\"\",\"session\":{\"new\":false},\"message\":{\"text\":\"Send
						// me the
						// answers\"},\"from\":{\"id\":\"1\",\"userInfo\":{\"firstName\":\"alf\",\"lastName\":\"angstrom\",\"email\":\"alf@example.com\"}}},\"variant\":{\"language\":null,\"mediaType\":{\"type\":\"application\",\"subtype\":\"json\",\"parameters\":{},\"wildcardType\":false,\"wildcardSubtype\":false},\"encoding\":null,\"languageString\":null},\"annotations\":[],\"language\":null,\"encoding\":null,\"mediaType\":{\"type\":\"application\",\"subtype\":\"json\",\"parameters\":{},\"wildcardType\":false,\"wildcardSubtype\":false}}"))
						.willReturn(aResponse().withHeader("Content-Type", "application/json").withBody(response)));

		wireMockRule
			.stubFor(post(urlPathMatching("/login/pubkey/authenticate"))
				.willReturn(aResponse()
						.withHeader("Content-Type", "application/json")
						.withBody("{\"token\": \"session123\"}")));

		wireMockRule
		.stubFor(post(urlPathMatching("/relay/pubkey/authenticate"))
			.willReturn(aResponse()
					.withHeader("Content-Type", "application/json")
					.withBody("{\"token\": \"km123\"}")));
		
		wireMockRule
		.stubFor(post(urlPathMatching("/agent/v4/stream/someblx/message/create"))
			.willReturn(aResponse()
					.withHeader("Content-Type", "application/json")
					.withBody("{}")));
		
		wireMockRule
		.stubFor(post(urlPathMatching("/agent/v4/datafeed/create"))
			.willReturn(aResponse()
					.withHeader("Content-Type", "application/json")
					.withBody("{\"id\": \"somedatafeedid\" }")));
		
		
		wireMockRule
		.stubFor(post(urlPathMatching("/agent/v4/datafeed/create"))
			.willReturn(aResponse()
					.withHeader("Content-Type", "application/json")
					.withBody("{\"id\": \"somedatafeedid\" }")));
		
		
		wireMockRule
		.stubFor(get(urlPathMatching("/agent/v4/datafeed/somedatafeedid/read"))
			.willReturn(aResponse()
					.withHeader("Content-Type", "application/json")
					.withFixedDelay(50000)
					.withBody("{}")));
		
		wireMockRule
		.stubFor(post(urlPathMatching("/agent/v4/stream/ABC123/message/create"))
			.willReturn(aResponse()
					.withHeader("Content-Type", "application/json")
					.withBody("{}")));
		
		wireMockRule.start();
	
//		
//		Mockito.when(usersApi.v1UserGet(Mockito.anyString(), Mockito.isNull(), Mockito.anyBoolean()))
//			.then((a) -> {
//				return new User().emailAddress("some.bot@example.com");
//			});*/
	}
	
	@AfterClass
	public static void close() {
		wireMockRule.shutdown();
	}
}
