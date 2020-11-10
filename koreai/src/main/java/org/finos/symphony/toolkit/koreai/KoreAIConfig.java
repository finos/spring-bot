package org.finos.symphony.toolkit.koreai;

import java.io.IOException;

import org.finos.symphony.toolkit.koreai.request.KoreAIRequester;
import org.finos.symphony.toolkit.koreai.request.KoreAIRequesterImpl;
import org.finos.symphony.toolkit.koreai.response.KoreAIResponseHandler;
import org.finos.symphony.toolkit.koreai.response.KoreAIResponseHandlerImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.symphony.api.agent.MessagesApi;
import com.symphony.api.id.SymphonyIdentity;

@Configuration
@EnableConfigurationProperties(KoreaiProperties.class)
public class KoreAIConfig {

	@Autowired
	KoreaiProperties properties;
	
	@Autowired
	SymphonyIdentity botIdentity;
	
	@Autowired
	MessagesApi messagesApi;
	
	@Autowired
	ResourceLoader rl;
	
	@Bean
	public ObjectMapper symphonyObjectMapper() {
		return new ObjectMapper();
	}
		
	@Bean
	public KoreAIResponseHandler responseMessageAdapter() throws IOException {
		return new KoreAIResponseHandlerImpl(messagesApi, rl, properties.isSkipEmptyResponses());	
	}
	
	@Bean
	public KoreAIRequester koreAIRequester(KoreAIResponseHandler responseHandler) throws Exception {
		return new KoreAIRequesterImpl(responseHandler, properties.getUrl(), JsonNodeFactory.instance, properties.getJwt(), new ObjectMapper());
	}
	
	@Bean
	public KoreAIEventHandler koreAIEventHandler(KoreAIRequester requester) {
		return new KoreAIEventHandler(botIdentity, requester);
	}
}
