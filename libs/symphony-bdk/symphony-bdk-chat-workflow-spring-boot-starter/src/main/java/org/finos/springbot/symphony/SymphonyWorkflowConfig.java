package org.finos.springbot.symphony;

import java.util.List;
import java.util.function.BiFunction;

import org.finos.springbot.ChatWorkflowConfig;
import org.finos.springbot.symphony.content.SymphonyContentConfig;
import org.finos.springbot.symphony.content.SymphonyUser;
import org.finos.springbot.symphony.content.serialization.MessageMLParser;
import org.finos.springbot.symphony.content.serialization.SymphonyMarkupWriter;
import org.finos.springbot.symphony.conversations.RoomMembershipChangeHandler;
import org.finos.springbot.symphony.conversations.StreamResolver;
import org.finos.springbot.symphony.conversations.SymphonyConversations;
import org.finos.springbot.symphony.conversations.SymphonyConversationsImpl;
import org.finos.springbot.symphony.data.SymphonyDataHandlerCofig;
import org.finos.springbot.symphony.form.ElementsHandler;
import org.finos.springbot.symphony.form.SymphonyFormConverter;
import org.finos.springbot.symphony.form.SymphonyFormDeserializerModule;
import org.finos.springbot.symphony.history.SymphonyHistory;
import org.finos.springbot.symphony.history.SymphonyHistoryImpl;
import org.finos.springbot.symphony.messages.PresentationMLHandler;
import org.finos.springbot.symphony.response.handlers.SymphonyResponseHandler;
import org.finos.springbot.symphony.response.templating.SymphonyMarkupTemplateProvider;
import org.finos.springbot.symphony.templating.FreemarkerTypeConverterConfig;
import org.finos.springbot.symphony.templating.FreemarkerWorkTemplater;
import org.finos.springbot.symphony.templating.SymphonyTemplateProvider;
import org.finos.springbot.workflow.actions.consumers.ActionConsumer;
import org.finos.springbot.workflow.actions.consumers.AddressingChecker;
import org.finos.springbot.workflow.actions.consumers.InRoomAddressingChecker;
import org.finos.springbot.workflow.content.Content;
import org.finos.springbot.workflow.data.EntityJsonConverter;
import org.finos.springbot.workflow.form.FormValidationProcessor;
import org.finos.springbot.workflow.response.handlers.ResponseHandlers;
import org.finos.springbot.workflow.response.templating.AbstractMarkupTemplateProvider;
import org.finos.springbot.workflow.response.templating.Markup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ResourceLoader;
import org.springframework.validation.Validator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.symphony.bdk.core.config.model.BdkBotConfig;
import com.symphony.bdk.core.config.model.BdkConfig;
import com.symphony.bdk.core.service.message.MessageService;
import com.symphony.bdk.core.service.stream.StreamService;
import com.symphony.bdk.core.service.user.UserService;
import com.symphony.bdk.gen.api.RoomMembershipApi;
import com.symphony.bdk.spring.SymphonyBdkAutoConfiguration;

/**
 * Symphony beans needing the workflow bean to be defined.
 * 
 * @author moffrob
 *
 */
@Configuration
@Import({
	SymphonyBdkAutoConfiguration.class,
	ChatWorkflowConfig.class, 
	FreemarkerTypeConverterConfig.class, 
	SymphonyContentConfig.class,
	SymphonyDataHandlerCofig.class})
@Profile(value = "symphony")
public class SymphonyWorkflowConfig {
	
	@Autowired
	MessageService messagesApi; 
	
	@Autowired
	RoomMembershipApi roomMembershipApi;
	
	@Autowired
	StreamService streamsApi;

	@Autowired
	Validator validator;
	
	@Autowired
	ResourceLoader resourceLoader;
	
	@Autowired
	EntityJsonConverter ejc;
	
	@Autowired 
	MessageMLParser messageMLParser;
	
	@Bean 
	@ConditionalOnMissingBean
	public SymphonyMarkupTemplateProvider symphonyMarkupTemplater(
			@Value("${symphony.templates.prefix:classpath:/templates/symphony/}") String prefix,
			@Value("${symphony.templates.suffix:.ftl}") String suffix,
			SymphonyMarkupWriter converter) {
		BiFunction<Content, Markup, String> cms = converter;
 		return new SymphonyMarkupTemplateProvider(prefix, suffix, resourceLoader, cms);
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
			AbstractMarkupTemplateProvider<Markup> markupTemplater,
			SymphonyTemplateProvider workTemplater,
			StreamResolver sr) {
		return new SymphonyResponseHandler(messagesApi,
				ejc, 
				markupTemplater,
				workTemplater,
				sr);
	}
	
	@Bean
	@ConditionalOnMissingBean
	public SymphonyHistory symphonyHistory(StreamResolver sr) {
		return new SymphonyHistoryImpl(ejc, messagesApi, sr);
	}
	
	@Bean 
	@ConditionalOnMissingBean
	public SymphonyConversations symphonyConversations(
			BdkConfig config, 
			UserService userService,
			@Value("${bot.local-pod-lookup:true}") boolean localPodLookup) {
		return new SymphonyConversationsImpl(streamsApi, userService, config.getBot(), localPodLookup);
	}
	
	@Bean
	@ConditionalOnMissingBean
	public PresentationMLHandler symphonyPresentationMLHandler(List<ActionConsumer> messageConsumers, SymphonyConversations sc, BdkConfig config) {
		return new PresentationMLHandler(messageMLParser, ejc, messageConsumers, sc, config.getBot().getUsername());
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
	public AddressingChecker symphonyDefaultAddressingChecker(BdkConfig botConfig, SymphonyConversations sc) {
		SymphonyUser su = sc.loadUserByUsername(botConfig.getBot().getUsername());
		return new InRoomAddressingChecker(() -> su, true);
	}
	
	@Bean
	@ConditionalOnMissingBean
	public ElementsHandler symphonyElementsHandler(List<ActionConsumer> elementsConsumers, FormValidationProcessor fvp, SymphonyConversations sc) {
		return new ElementsHandler(messagesApi, ejc, symphonyFormConverter(), elementsConsumers, sc, fvp);
	}
	
	@Bean
	@ConditionalOnMissingBean
	public RoomMembershipChangeHandler roomMembershipChangeHandler(List<ActionConsumer> consumers, SymphonyConversations sc) {
		return new RoomMembershipChangeHandler(consumers, sc);
	}

}