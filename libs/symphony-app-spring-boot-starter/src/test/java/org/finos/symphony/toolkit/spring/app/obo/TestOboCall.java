package org.finos.symphony.toolkit.spring.app.obo;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;

import org.finos.symphony.toolkit.spring.api.factories.ApiInstance;
import org.finos.symphony.toolkit.spring.app.AbstractTest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.matching.EqualToPattern;
import com.github.tomakehurst.wiremock.matching.MultipartValuePatternBuilder;
import com.github.tomakehurst.wiremock.matching.RegexPattern;
import com.symphony.api.agent.MessagesApi;

@TestPropertySource(properties={
		"symphony.apis.0.id=7777",
		"symphony.apis.0.pod.url=http://localhost:8000/pod",
		"symphony.apis.0.agent.url=http://localhost:8000/agent",
		"symphony.apis.0.sessionAuth.url=http://localhost:8000/sessionauth",
		"symphony.app.store.location=src/test/resources/pods",
		"symphony.app.proxy.host=myproxy.com",
		"symphony.app.jwt=true"
	})
public class TestOboCall extends AbstractTest {

	private static final String APP_TOKEN = "abc123";
	private static final String OBO_TOKEN = "my-obo-token-abc-123-1234";
	private static final String STREAM_IDS = "123";
	private static final String MESSAGE_CONTENT = "<messageML>Yo ho</messageML>";

	@Autowired
	OboInstanceFactory oboInstanceFactory;
	
	public static WireMockServer wireMockRule = new WireMockServer(8000);
	
	@Test
	public void doSomethingOBODefaultPod() throws Exception {
		ApiInstance ai = oboInstanceFactory.createApiInstance(1234l);
		MessagesApi messagesApi = ai.getAgentApi(MessagesApi.class);
		messagesApi.v4MessageBlastPost(STREAM_IDS,MESSAGE_CONTENT, null, null, null, null, null, null);
	}
	
	@Test
	public void doSomethingOBORSA() throws Exception {
		ApiInstance ai = oboInstanceFactory.createApiInstance(1234l, "1000");
		MessagesApi messagesApi = ai.getAgentApi(MessagesApi.class);
		messagesApi.v4MessageBlastPost(STREAM_IDS,MESSAGE_CONTENT, null, null, null, null, null, null);
	}
	
	@Test
	public void doSomethingOBOCert() throws Exception {
		ApiInstance ai = oboInstanceFactory.createApiInstance(1234l, "1001");
		MessagesApi messagesApi = ai.getAgentApi(MessagesApi.class);
		messagesApi.v4MessageBlastPost(STREAM_IDS,MESSAGE_CONTENT, null, null, null, null, null, null);
	}
		
	@BeforeEach
	public void setupWireMock() {
		
		// session auth / cert login
		
		wireMockRule.stubFor(post(urlEqualTo("/sessionauth/v1/app/authenticate"))
			.willReturn(aResponse()
				.withHeader("Content-Type", "application/json")
				.withBody("{\"name\": \"some-app-token\", \"token\": \""+APP_TOKEN+"\"}")));
		
		wireMockRule.stubFor(
				post(urlEqualTo("/sessionauth/v1/app/user/1234/authenticate"))
				.withHeader("sessionToken", new EqualToPattern(APP_TOKEN))
				.willReturn(aResponse()
					.withHeader("Content-Type", "application/json")
					.withBody("{\"sessionToken\": \""+OBO_TOKEN+"\"}")));
			
		// rsa auth 
		
		wireMockRule.stubFor(
				post(urlEqualTo("/login/pubkey/authenticate"))
				.withRequestBody(new RegexPattern("\\{\"token\":\".*}"))
				.willReturn(aResponse()
					.withHeader("Content-Type", "application/json")
					.withBody("{\"name\": \"some-app-token\", \"token\": \""+APP_TOKEN+"\"}")));
			
		
		wireMockRule.stubFor(
				post(urlEqualTo("/login/pubkey/app/user/1234/authenticate"))
				.withHeader("sessionToken", new EqualToPattern(APP_TOKEN))
				.willReturn(aResponse()
					.withHeader("Content-Type", "application/json")
					.withBody("{\"token\": \""+OBO_TOKEN+"\"}")));
		
		
		
		// sending the blast message
		
		wireMockRule.stubFor(
				post(urlEqualTo("/agent/v4/message/blast"))
				.withMultipartRequestBody(new MultipartValuePatternBuilder("sids").withBody(new EqualToPattern(STREAM_IDS)))
				.withMultipartRequestBody(new MultipartValuePatternBuilder("message").withBody(new EqualToPattern(MESSAGE_CONTENT)))
				.withHeader("sessionToken", new EqualToPattern(OBO_TOKEN))
				.willReturn(aResponse()
						.withHeader("Content-Type", "application/json")
						.withStatus(200)));
		
		wireMockRule.start();
	}
	
	
	@AfterAll
	public static void shutdownWireMock() {
		wireMockRule.shutdown();
	}
	
	
}
