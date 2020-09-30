package com.github.deutschebank.symphony.workflow.sources.symphony;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.validation.Validator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.deutschebank.symphony.spring.api.SymphonyApiConfig;
import com.github.deutschebank.symphony.stream.spring.SharedStreamConfig;
import com.github.deutschebank.symphony.workflow.Workflow;
import com.github.deutschebank.symphony.workflow.history.History;
import com.github.deutschebank.symphony.workflow.sources.symphony.elements.ElementsConsumer;
import com.github.deutschebank.symphony.workflow.sources.symphony.elements.ElementsHandler;
import com.github.deutschebank.symphony.workflow.sources.symphony.elements.FormConverter;
import com.github.deutschebank.symphony.workflow.sources.symphony.handlers.AttachmentHandler;
import com.github.deutschebank.symphony.workflow.sources.symphony.handlers.EntityJsonConverter;
import com.github.deutschebank.symphony.workflow.sources.symphony.handlers.FormMessageMLConverter;
import com.github.deutschebank.symphony.workflow.sources.symphony.handlers.FreemarkerFormMessageMLConverter;
import com.github.deutschebank.symphony.workflow.sources.symphony.handlers.SymphonyResponseHandler;
import com.github.deutschebank.symphony.workflow.sources.symphony.history.MessageHistory;
import com.github.deutschebank.symphony.workflow.sources.symphony.messages.PresentationMLHandler;
import com.github.deutschebank.symphony.workflow.sources.symphony.messages.SimpleMessageConsumer;
import com.github.deutschebank.symphony.workflow.sources.symphony.messages.SimpleMessageParser;
import com.github.deutschebank.symphony.workflow.sources.symphony.room.SymphonyRooms;
import com.github.deutschebank.symphony.workflow.sources.symphony.room.SymphonyRoomsImpl;
import com.symphony.api.agent.MessagesApi;
import com.symphony.api.id.SymphonyIdentity;
import com.symphony.api.pod.RoomMembershipApi;
import com.symphony.api.pod.StreamsApi;
import com.symphony.api.pod.UsersApi;

@Configuration
@AutoConfigureBefore(SharedStreamConfig.class)
public class SymphonyWorkflowConfig {
	
	@Autowired
	@Qualifier(SymphonyApiConfig.BOT_IDENTITY)
	SymphonyIdentity botIdentity;
	
	@Autowired
	UsersApi usersApi;
	
	@Autowired
	MessagesApi messagesApi; 
	
	@Autowired
	RoomMembershipApi roomMembershipApi;
	
	@Autowired
	StreamsApi streamsApi;

	@Autowired
	Workflow wf;
	
	@Autowired
	Validator validator;
	
	@Autowired
	AttachmentHandler attachmentHandler;
	
	@Autowired
	ResourceLoader resourceLoader;
	
	@Bean
	@ConditionalOnMissingBean
	public SimpleMessageParser simpleMessageParser() {
		return new SimpleMessageParser();
	}
	
	@Bean
	@ConditionalOnMissingBean
	public SymphonyResponseHandler symphonyResponseHandler() {
		return new SymphonyResponseHandler(messagesApi, formMessageMLConverter(), entityJsonConverter(), symphonyRooms(), attachmentHandler);
	}
	
	@Bean
	@ConditionalOnMissingBean
	public FormMessageMLConverter formMessageMLConverter() {
		return new FreemarkerFormMessageMLConverter(symphonyRooms(), resourceLoader);
	}
	
	@Bean
	@ConditionalOnMissingBean
	public History symphonyHistory() {
		return new MessageHistory(wf, entityJsonConverter(), messagesApi, symphonyRooms());
	}
	
	@Bean
	@ConditionalOnMissingBean
	public SymphonyBot symphonyBot(List<SymphonyEventHandler> eventHandlers) {
		return new SymphonyBot(botIdentity, eventHandlers);
	}
	
	@Bean 
	@ConditionalOnMissingBean
	public SymphonyRooms symphonyRooms() {
		SymphonyRooms ru = new SymphonyRoomsImpl(wf, roomMembershipApi, streamsApi, usersApi);
		return ru;
	}
	
	@Bean
	@ConditionalOnMissingBean
	public EntityJsonConverter entityJsonConverter() {
		return new EntityJsonConverter(wf, new ObjectMapper());
	}
	
	@Bean
	public PresentationMLHandler presentationMLHandler(List<SimpleMessageConsumer> messageConsumers) {
		return new PresentationMLHandler(wf, botIdentity, usersApi, simpleMessageParser(), entityJsonConverter(), messageConsumers, symphonyResponseHandler(), symphonyRooms());
	}
	
	@Bean
	public ElementsHandler elementsHandler(List<ElementsConsumer> elementsConsumers) {
		return new ElementsHandler(wf, messagesApi, entityJsonConverter(), new FormConverter(symphonyRooms()), elementsConsumers, symphonyResponseHandler(), symphonyRooms(), validator);
	}
	
}