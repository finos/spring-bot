package com.github.deutschebank.symphony.koreai;

import java.io.IOException;
import java.nio.charset.Charset;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.StreamUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.github.deutschebank.symphony.koreai.request.KoreAIRequester;
import com.github.deutschebank.symphony.koreai.request.KoreAIRequesterImpl;
import com.github.deutschebank.symphony.koreai.response.KoreAIResponseHandler;
import com.github.deutschebank.symphony.koreai.response.KoreaiResponseMessageAdapter;
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
	
	@Autowired
	ObjectMapper om;
	
	@Bean
	public KoreAIResponseHandler responseMessageAdapter() throws IOException {
	  	String template = StreamUtils.copyToString(
	  			rl.getResource(properties.getTemplate()).getInputStream(), Charset.forName("UTF-8"));
		return new KoreaiResponseMessageAdapter(messagesApi, template);	
	}
	
	@Bean
	public KoreAIRequester koreAIRequester(KoreAIResponseHandler responseHandler) throws Exception {
		return new KoreAIRequesterImpl(responseHandler, properties.getUrl(), JsonNodeFactory.instance, properties.getJwt());
	}
	
	@Bean
	public KoreAIEventHandler koreAIEventHandler(KoreAIRequester requester) {
		return new KoreAIEventHandler(botIdentity, requester);
	}
}
