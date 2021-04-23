package org.finos.symphony.toolkit.koreai.request;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;

import org.finos.symphony.toolkit.koreai.Address;
import org.finos.symphony.toolkit.koreai.output.KoreAIResponseHandler;
import org.finos.symphony.toolkit.koreai.response.KoreAIResponse;
import org.finos.symphony.toolkit.koreai.response.KoreAIResponseBuilderImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.StreamUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.matching.EqualToPattern;
import com.google.common.base.Charsets;

@ExtendWith(SpringExtension.class)

public class KoreAIRequesterImplTest {
	
	public WireMockServer wireMockRule = new WireMockServer(9998);
	
	public KoreAIRequester requester;
	
	@MockBean
	KoreAIResponseHandler responseHandler;
	
	ObjectMapper om = new ObjectMapper();
	
	@Captor
	ArgumentCaptor<KoreAIResponse> response;
	
	@Captor
	ArgumentCaptor<Address> a;
	
	
	@BeforeEach
	public void setupWireMock() throws Exception {
		String response = StreamUtils.copyToString(KoreAIRequesterImplTest.class.getResourceAsStream("ans1.json"), Charsets.UTF_8);
		wireMockRule.stubFor(post(urlEqualTo("/kore"))
			.withHeader("Authorization", new EqualToPattern("Bearer some-jwt"))
			//.withRequestBody(new EqualToPattern("{\"entity\":{\"to\":\"\",\"session\":{\"new\":false},\"message\":{\"text\":\"Send me the answers\"},\"from\":{\"id\":\"1\",\"userInfo\":{\"firstName\":\"alf\",\"lastName\":\"angstrom\",\"email\":\"alf@example.com\"}}},\"variant\":{\"language\":null,\"mediaType\":{\"type\":\"application\",\"subtype\":\"json\",\"parameters\":{},\"wildcardType\":false,\"wildcardSubtype\":false},\"encoding\":null,\"languageString\":null},\"annotations\":[],\"language\":null,\"encoding\":null,\"mediaType\":{\"type\":\"application\",\"subtype\":\"json\",\"parameters\":{},\"wildcardType\":false,\"wildcardSubtype\":false}}"))
			.willReturn(aResponse()
				.withHeader("Content-Type", "application/json")
				.withBody(response)));
		requester = new KoreAIRequesterImpl(responseHandler, 
				new KoreAIResponseBuilderImpl(om, JsonNodeFactory.instance),
				"http://localhost:9998/kore", JsonNodeFactory.instance, "some-jwt", null);
		((KoreAIRequesterImpl) requester).afterPropertiesSet();
		wireMockRule.start();
	}
	
	@AfterEach
	public void closeWiremock() {
		wireMockRule.shutdown();
	}
	
	@Test
	public void testRequester( ) {
    	Address a = new Address(1l, "alf", "angstrom", "alf@example.com", "abc1234");
		requester.send(a, "Send me the answers");
		Mockito.verify(responseHandler).handle(this.a.capture(), this.response.capture());
		Assertions.assertEquals(a, this.a.getValue());
		Assertions.assertTrue(this.response.getValue().getProcessed().get(0).toPrettyString().contains("Looks like your application"));
		
	}
}
