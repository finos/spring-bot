package org.finos.symphony.toolkit.spring.app.tokens.pod;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;

import org.finos.symphony.toolkit.spring.app.tokens.pod.ConfiguredPodTokenStrategy;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.symphony.api.model.ExtensionAppTokens;

@RunWith(SpringRunner.class)
@SpringBootTest()
@ActiveProfiles({"inttest"})
@ContextConfiguration
public class TokenRequestIntegrationTest  {
	
	@Autowired
	ConfiguredPodTokenStrategy cpts;
	
	@Rule
	public WireMockRule wireMockRule = new WireMockRule(9999);
	
	@Before
	public void setupWireMock() {
		wireMockRule.stubFor(post(urlEqualTo("/sessionauth/v1/authenticate/extensionApp"))
			.willReturn(aResponse()
				.withHeader("Content-Type", "application/json")
				.withBody("{\"appId\": \"symphony-practice-app\", \"appToken\": \"abc123\", \"symphonyToken\": \"sym123\"}")));
	}
	
	@Test
	public void doRequestToken() throws Exception {
		ExtensionAppTokens eat = cpts.getTokens("abc123", "develop");
		Assert.assertNotNull(eat.getSymphonyToken());
		Assert.assertEquals("symphony-practice-app", eat.getAppId());
		Assert.assertEquals("abc123", eat.getAppToken());
	}
	
}
