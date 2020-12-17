package org.finos.symphony.toolkit.koreai.bot;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;

import java.util.ArrayList;
import java.util.Collections;

import org.finos.symphony.toolkit.json.EntityJson;
import org.finos.symphony.toolkit.koreai.KoreAIBot;
import org.finos.symphony.toolkit.koreai.spring.KoreAIEventHandler;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
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
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.github.tomakehurst.wiremock.matching.EqualToPattern;
import com.google.common.base.Charsets;
import com.symphony.api.id.testing.TestIdentityProvider;
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
public class TestBot {

	@Rule
	public WireMockRule wireMockRule = new WireMockRule(WireMockConfiguration.wireMockConfig().port(9999), true);

	
	@Autowired
	ApplicationContext ctx;
	

	int messagesSent = 0;

	/**
	 * When the user presses a button, we return a response.
	 */
	@Test
	public void testPressButton() {
		KoreAIEventHandler eventHandler = ctx.getBean(KoreAIEventHandler.class);
		V4Event in = new V4Event()
				.initiator(new V4Initiator()
						.user(new V4User().email("rob@example.com").displayName("Rob Example").userId(2438923l)))
				.payload(new V4Payload().symphonyElementsAction(new V4SymphonyElementsAction().formId("koreai-choice")
						.formValues(Collections.singletonMap("action", "some button"))
						.stream(new V4Stream().streamId("ABC123"))));

		eventHandler.accept(in);
		Assert.assertEquals(1, messagesSent);
	}
/*
	@Test
	public void testSendMessageIM() {
		V4Event in = new V4Event()
				.initiator(new V4Initiator()
						.user(new V4User().email("rob@example.com").displayName("Rob Example").userId(2438923l)))
				.payload(new V4Payload().messageSent(new V4MessageSent().message(
						new V4Message().message("<div>hello</div>").data("{}").stream(new V4Stream().streamId("ABC123").streamType("IM")))));

		eventHandler.accept(in);
		Assert.assertEquals(1, messagesSent);
	}
	
	@Test
	public void testSendMessageRoom() throws JsonProcessingException {
		Mention m = new Mention();
		m.setId(new ArrayList<>());
		m.getId().add(new UserId("123"));
		EntityJson ej = new EntityJson();
		ej.put("m1", m);
		String data = symphonyObjectMapper.writeValueAsString(ej);
		eventHandler.setBotUserId(123l);
		V4Event in = new V4Event()
				.initiator(new V4Initiator()
						.user(new V4User().email("rob@example.com").displayName("Rob Example").userId(2438923l)))
				.payload(new V4Payload().messageSent(new V4MessageSent()
						.message(new V4Message().message("<div>hello</div>")
								.stream(new V4Stream().streamId("ABC123").streamType("ROOM"))
								.data(data))));
					

		eventHandler.accept(in);
		Assert.assertEquals(1, messagesSent);
	}*/
	
	@BeforeClass
	public static void ensureNoTestIdentity() {
		System.getProperties().setProperty(TestIdentityProvider.TEST_IDENTITY_PROPERTY, TestIdentityProvider.NO_VALUE);
		
	}

	@Before
	public void setupWireMockKore() throws Exception {
		String response = StreamUtils.copyToString(TestBot.class.getResourceAsStream("ans1.json"), Charsets.UTF_8);
		wireMockRule
				.stubFor(post(urlEqualTo("/kore")).withHeader("Authorization", new EqualToPattern("Bearer some-jwt"))
						// .withRequestBody(new
						// EqualToPattern("{\"entity\":{\"to\":\"\",\"session\":{\"new\":false},\"message\":{\"text\":\"Send
						// me the
						// answers\"},\"from\":{\"id\":\"1\",\"userInfo\":{\"firstName\":\"alf\",\"lastName\":\"angstrom\",\"email\":\"alf@example.com\"}}},\"variant\":{\"language\":null,\"mediaType\":{\"type\":\"application\",\"subtype\":\"json\",\"parameters\":{},\"wildcardType\":false,\"wildcardSubtype\":false},\"encoding\":null,\"languageString\":null},\"annotations\":[],\"language\":null,\"encoding\":null,\"mediaType\":{\"type\":\"application\",\"subtype\":\"json\",\"parameters\":{},\"wildcardType\":false,\"wildcardSubtype\":false}}"))
						.willReturn(aResponse().withHeader("Content-Type", "application/json").withBody(response)));
	}

	@Before
	public void setupWireMockSymphony() throws Exception {
		wireMockRule
			.stubFor(post(urlEqualTo("/login"))
				.willReturn(aResponse().withBody("{\"token\": \"abc123\"}")));

		wireMockRule
		.stubFor(post(urlEqualTo("/relay"))
			.willReturn(aResponse().withBody("{\"token\": \"abc123\"}")));

			
		/*Mockito.when(messages.v4StreamSidMessageCreatePost(Mockito.isNull(), Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString(), Mockito.isNull(), Mockito.isNull(), Mockito.isNull(), Mockito.isNull()))
				.then((a) -> {
					messagesSent++;
					return null;
				});
		messagesSent = 0;
		
		Mockito.when(usersApi.v1UserGet(Mockito.anyString(), Mockito.isNull(), Mockito.anyBoolean()))
			.then((a) -> {
				return new User().emailAddress("some.bot@example.com");
			});*/
	}
}
