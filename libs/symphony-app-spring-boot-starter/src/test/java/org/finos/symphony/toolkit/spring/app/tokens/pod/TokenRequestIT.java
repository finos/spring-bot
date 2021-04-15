package org.finos.symphony.toolkit.spring.app.tokens.pod;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.symphony.api.model.ExtensionAppTokens;

@ExtendWith(SpringExtension.class)
@SpringBootTest()
@ActiveProfiles({"inttest"})
@ContextConfiguration
public class TokenRequestIT  {
	
	@Autowired
	ConfiguredPodTokenStrategy cpts;
	
	public WireMockServer wireMockRule = new WireMockServer(9999);
	
	@BeforeEach
	public void setupWireMock() {
		
		wireMockRule.stubFor(post(urlEqualTo("/sessionauth/v1/authenticate/extensionApp"))
			.willReturn(aResponse()
				.withHeader("Content-Type", "application/json")
				.withBody("{\"appId\": \"symphony-practice-app\", \"appToken\": \"abc123\", \"symphonyToken\": \"sym123\"}")));
		wireMockRule.start();
	}
	
	@Test
	public void doRequestToken() throws Exception {
		ExtensionAppTokens eat = cpts.getTokens("abc123", "develop");
		Assertions.assertNotNull(eat.getSymphonyToken());
		Assertions.assertEquals("symphony-practice-app", eat.getAppId());
		Assertions.assertEquals("abc123", eat.getAppToken());
	}
	
	@AfterEach
	public void shutdownWireMock() {
		wireMockRule.shutdown();
	}
	
	
}
