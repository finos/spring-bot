package org.finos.symphony.toolkit.koreai;

import java.io.IOException;

import org.finos.symphony.toolkit.json.EntityJsonTypeResolverBuilder.VersionSpace;
import org.finos.symphony.toolkit.json.ObjectMapperFactory;
import org.finos.symphony.toolkit.koreai.output.KoreAIResponseHandler;
import org.finos.symphony.toolkit.koreai.output.KoreAIResponseHandlerImpl;
import org.finos.symphony.toolkit.koreai.request.KoreAIRequester;
import org.finos.symphony.toolkit.koreai.request.KoreAIRequesterImpl;
import org.finos.symphony.toolkit.koreai.response.KoreAIResponse;
import org.finos.symphony.toolkit.koreai.response.KoreAIResponseBuilder;
import org.finos.symphony.toolkit.koreai.response.KoreAIResponseBuilderImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.symphony.api.agent.MessagesApi;
import com.symphony.api.id.SymphonyIdentity;
import com.symphony.api.model.User;
import com.symphony.api.pod.UsersApi;

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
	
	@Autowired
	UsersApi usersApi;
	
	@Bean
	public ObjectMapper symphonyObjectMapper() {
		return koreAIObjectMapper();
	}

	public static ObjectMapper koreAIObjectMapper() {
		ObjectMapper out = new ObjectMapper();
		ObjectMapperFactory.initialize(out, ObjectMapperFactory
			.extendedSymphonyVersionSpace(
				new VersionSpace(KoreAIResponse.class.getPackage().getName(), "1.0"),
				new VersionSpace(ObjectNode.class.getPackage().getName(), "1.0")));
		return out;
	}
		
	@Bean
	public KoreAIResponseHandler responseMessageAdapter() throws IOException {
		return new KoreAIResponseHandlerImpl(messagesApi, rl, 
				properties.isSkipEmptyResponses(), 
				symphonyObjectMapper(),
				properties.getTemplatePrefix());	
	}
	
	@Bean
	public KoreAIRequester koreAIRequester(KoreAIResponseHandler responseHandler, KoreAIResponseBuilder responseBuilder) throws Exception {
		return new KoreAIRequesterImpl(responseHandler,
				responseBuilder,
				properties.getUrl(), 
				JsonNodeFactory.instance, 
				properties.getJwt());
	}
	
	@Bean
	public KoreAIResponseBuilder koreAIResponseBuilder() {
		return new KoreAIResponseBuilderImpl(new ObjectMapper(), JsonNodeFactory.instance);
	}
	
	@Bean
	public KoreAIEventHandler koreAIEventHandler(KoreAIRequester requester) {
		return new KoreAIEventHandler(botIdentity, usersApi, requester, symphonyObjectMapper(), properties.isOnlyAddressed());
	}
}
