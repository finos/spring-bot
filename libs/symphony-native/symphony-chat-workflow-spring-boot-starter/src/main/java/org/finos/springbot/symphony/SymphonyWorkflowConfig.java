package org.finos.springbot.symphony;

import java.util.List;

import org.finos.springbot.ChatWorkflowConfig;
import org.finos.springbot.symphony.content.SymphonyContentConfig;
import org.finos.springbot.symphony.content.SymphonyUser;
import org.finos.springbot.symphony.content.serialization.MessageMLParser;
import org.finos.springbot.symphony.conversations.SymphonyConversations;
import org.finos.springbot.symphony.conversations.SymphonyConversationsImpl;
import org.finos.springbot.symphony.form.ElementsHandler;
import org.finos.springbot.symphony.form.SymphonyFormConverter;
import org.finos.springbot.symphony.form.SymphonyFormDeserializerModule;
import org.finos.springbot.symphony.history.SymphonyHistory;
import org.finos.springbot.symphony.history.SymphonyHistoryImpl;
import org.finos.springbot.symphony.json.DataHandlerCofig;
import org.finos.springbot.symphony.json.EntityJsonConverter;
import org.finos.springbot.symphony.messages.PresentationMLHandler;
import org.finos.springbot.symphony.response.handlers.AttachmentHandler;
import org.finos.springbot.symphony.response.handlers.HeaderTagResponseHandler;
import org.finos.springbot.symphony.response.handlers.JerseyAttachmentHandlerConfig;
import org.finos.springbot.symphony.response.handlers.SymphonyResponseHandler;
import org.finos.springbot.symphony.templating.FreemarkerTypeConverterConfig;
import org.finos.springbot.symphony.templating.FreemarkerWorkTemplater;
import org.finos.springbot.symphony.templating.SymphonyTemplateProvider;
import org.finos.springbot.workflow.actions.consumers.ActionConsumer;
import org.finos.springbot.workflow.actions.consumers.AddressingChecker;
import org.finos.springbot.workflow.actions.consumers.InRoomAddressingChecker;
import org.finos.springbot.workflow.content.serialization.MarkupWriter;
import org.finos.springbot.workflow.form.FormValidationProcessor;
import org.finos.springbot.workflow.response.templating.SimpleMessageMarkupTemplateProvider;
import org.finos.symphony.toolkit.spring.api.SymphonyApiConfig;
import org.finos.symphony.toolkit.stream.single.SharedStreamSingleBotConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ResourceLoader;
import org.springframework.validation.Validator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.symphony.api.agent.MessagesApi;
import com.symphony.api.id.SymphonyIdentity;
import com.symphony.api.model.UserV2;
import com.symphony.api.pod.RoomMembershipApi;
import com.symphony.api.pod.StreamsApi;
import com.symphony.api.pod.UsersApi;

/**
 * Symphony beans needing the workflow bean to be defined.
 * 
 * @author moffrob
 *
 */
@Configuration
@AutoConfigureBefore(SharedStreamSingleBotConfig.class)
@Import({
	ChatWorkflowConfig.class, 
	FreemarkerTypeConverterConfig.class, 
	JerseyAttachmentHandlerConfig.class,
	SymphonyContentConfig.class,
	DataHandlerCofig.class})
@ConditionalOnProperty("symphony.apis.0.pod.url")
public class SymphonyWorkflowConfig {
		
	@Autowired
	@Qualifier(SymphonyApiConfig.SINGLE_BOT_IDENTITY_BEAN)
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
	Validator validator;
	
	@Autowired
	AttachmentHandler attachmentHandler;
	
	@Autowired
	ResourceLoader resourceLoader;
	
	@Autowired
	EntityJsonConverter ejc;
	
	@Autowired 
	MessageMLParser messageMLParser;
	
	@Bean 
	@ConditionalOnMissingBean
	public SimpleMessageMarkupTemplateProvider symphonyMarkupTemplater(
			@Value("${symphony.templates.prefix:classpath:/templates/symphony/}") String prefix,
			@Value("${symphony.templates.suffix:.ftl}") String suffix,
			MarkupWriter converter) {
		return new SimpleMessageMarkupTemplateProvider(prefix, suffix, resourceLoader, converter);
	}
	
	@Bean
	@ConditionalOnMissingBean
	public SymphonyTemplateProvider symphonyWorkTemplater(
			@Value("${symphony.templates.prefix:classpath:/templates/symphony/}") String prefix,
			@Value("${symphony.templates.suffix:.ftl}") String suffix,
			FreemarkerWorkTemplater formConverter) {
		return new SymphonyTemplateProvider(prefix, suffix, resourceLoader, formConverter);
	}
	
	@Bean
	@ConditionalOnMissingBean
	public SymphonyResponseHandler symphonyResponseHandler(
			SimpleMessageMarkupTemplateProvider markupTemplater,
			SymphonyTemplateProvider workTemplater) {
		return new SymphonyResponseHandler(messagesApi, streamsApi, usersApi, 
				ejc, 
				attachmentHandler, 
				markupTemplater,
				workTemplater);
	}

	
	@Bean
	@ConditionalOnMissingBean
	public SymphonyHistory symphonyHistory() {
		return new SymphonyHistoryImpl(ejc, messagesApi, streamsApi, usersApi);
	}
	
	@Bean 
	@ConditionalOnMissingBean
	public SymphonyConversations symphonyRooms() {
		return new SymphonyConversationsImpl(roomMembershipApi, streamsApi, usersApi, botIdentity);
	}
	
	@Bean
	@ConditionalOnMissingBean
	public HeaderTagResponseHandler symphonyHeaderTagResponsehandler() {
		return new HeaderTagResponseHandler();
	} 
	
	
	
	@Bean
	@ConditionalOnMissingBean
	public PresentationMLHandler symphonyPresentationMLHandler(List<ActionConsumer> messageConsumers) {
		return new PresentationMLHandler(messageMLParser, ejc, messageConsumers, symphonyRooms(), botIdentity);
	}

	@Bean
	@ConditionalOnMissingBean
	public SymphonyFormConverter symphonyFormConverter() {
		ObjectMapper om = new ObjectMapper();
		om.registerModule(new SymphonyFormDeserializerModule());
		om.registerModule(new JavaTimeModule());
		return new SymphonyFormConverter(om);
	}
	
	@Bean
	@ConditionalOnMissingBean
	public AddressingChecker symphonyDefaultAddressingChecker() {
		UserV2 symphonyBotUser = usersApi.v2UserGet(null, null, botIdentity.getEmail(), null, true);
		SymphonyUser su = new SymphonyUser(symphonyBotUser.getDisplayName(), symphonyBotUser.getEmailAddress());
		return new InRoomAddressingChecker(() -> su, true);
	}
	
	@Bean
	@ConditionalOnMissingBean
	public ElementsHandler symphonyElementsHandler(List<ActionConsumer> elementsConsumers, FormValidationProcessor fvp) {
		return new ElementsHandler(messagesApi, ejc, symphonyFormConverter(), elementsConsumers, symphonyRooms(), fvp);
	}

}