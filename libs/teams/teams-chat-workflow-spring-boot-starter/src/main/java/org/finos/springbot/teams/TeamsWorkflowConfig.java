package org.finos.springbot.teams;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import org.finos.springbot.ChatWorkflowConfig;
import org.finos.springbot.teams.content.TeamsContentConfig;
import org.finos.springbot.teams.content.serialization.TeamsHTMLParser;
import org.finos.springbot.teams.content.serialization.TeamsMarkupWriter;
import org.finos.springbot.teams.conversations.TeamsConversations;
import org.finos.springbot.teams.conversations.TeamsConversationsImpl;
import org.finos.springbot.teams.form.TeamsFormConverter;
import org.finos.springbot.teams.form.TeamsFormDeserializerModule;
import org.finos.springbot.teams.handlers.TeamsResponseHandler;
import org.finos.springbot.teams.history.AzureBlobStorageTeamsHistory;
import org.finos.springbot.teams.history.MemoryTeamsHistory;
import org.finos.springbot.teams.history.StorageIDResponseHandler;
import org.finos.springbot.teams.history.TeamsHistory;
import org.finos.springbot.teams.messages.MessageActivityHandler;
import org.finos.springbot.teams.response.templating.EntityMarkupTemplateProvider;
import org.finos.springbot.teams.templating.adaptivecard.AdaptiveCardConverterConfig;
import org.finos.springbot.teams.templating.adaptivecard.AdaptiveCardTemplateProvider;
import org.finos.springbot.teams.templating.adaptivecard.AdaptiveCardTemplater;
import org.finos.springbot.teams.templating.thymeleaf.ThymeleafConverterConfig;
import org.finos.springbot.teams.templating.thymeleaf.ThymeleafTemplateProvider;
import org.finos.springbot.teams.templating.thymeleaf.ThymeleafTemplater;
import org.finos.springbot.teams.turns.CurrentTurnContext;
import org.finos.springbot.workflow.actions.consumers.ActionConsumer;
import org.finos.springbot.workflow.actions.consumers.AddressingChecker;
import org.finos.springbot.workflow.actions.consumers.InRoomAddressingChecker;
import org.finos.springbot.workflow.content.User;
import org.finos.springbot.workflow.data.EntityJsonConverter;
import org.finos.springbot.workflow.form.FormValidationProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.StringUtils;
import org.springframework.validation.Validator;

import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.microsoft.bot.builder.BotFrameworkAdapter;
import com.microsoft.bot.builder.TurnContext;
import com.microsoft.bot.connector.authentication.MicrosoftAppCredentials;
import com.microsoft.bot.integration.AdapterWithErrorHandler;
import com.microsoft.bot.integration.BotFrameworkHttpAdapter;
import com.microsoft.bot.integration.spring.BotController;
import com.microsoft.bot.integration.spring.BotDependencyConfiguration;
import com.microsoft.bot.schema.ChannelAccount;

/**
 * Symphony beans needing the workflow bean to be defined.
 * 
 * @author moffrob
 *
 */
@Configuration
@Import({
	ChatWorkflowConfig.class, 
	TeamsContentConfig.class,
	AdaptiveCardConverterConfig.class,
	ThymeleafConverterConfig.class
})
@Profile("teams")
public class TeamsWorkflowConfig extends BotDependencyConfiguration {
		
	private static final Logger LOG = LoggerFactory.getLogger(TeamsWorkflowConfig.class);
	
	@Autowired
	Validator validator;
	
	@Autowired
	ResourceLoader resourceLoader;
	
	@Autowired
	ApplicationContext ac;
	
	@Autowired
	EntityJsonConverter ejc;
	
	@Bean 
	@ConditionalOnMissingBean
	public EntityMarkupTemplateProvider teamsMarkupTemplater(
			@Value("${teams.templates.prefix:classpath:/templates/teams/}") String prefix,
			@Value("${teams.templates.suffix:.html}") String suffix,
			TeamsMarkupWriter converter) {
		return new EntityMarkupTemplateProvider(prefix, suffix, resourceLoader, converter);
	}
	
	@Bean
	@ConditionalOnMissingBean
	public AdaptiveCardTemplateProvider adaptiveCardWorkTemplater(
			@Value("${teams.templates.prefix:classpath:/templates/teams/}") String prefix,
			@Value("${teams.templates.suffix:.json}") String suffix,
			AdaptiveCardTemplater formConverter) throws IOException {
		return new AdaptiveCardTemplateProvider(prefix, suffix, resourceLoader, formConverter);
	}
	
	@Bean
	@ConditionalOnMissingBean
	public ThymeleafTemplateProvider thymeleafWorkTemplater(
			@Value("${teams.templates.prefix:classpath:/templates/teams/}") String prefix,
			@Value("${teams.templates.suffix:.html}") String suffix,
			ThymeleafTemplater formConverter) throws IOException {
		return new ThymeleafTemplateProvider(prefix, suffix, resourceLoader, formConverter);
	}
	
	@Bean
	@ConditionalOnMissingBean
	public TeamsResponseHandler teamsResponseHandler(
			EntityMarkupTemplateProvider markupTemplater,
			AdaptiveCardTemplateProvider formTemplater,
			ThymeleafTemplateProvider displayTemplater,
			TeamsHistory th,
			TeamsConversations tc) {
		return new TeamsResponseHandler(
				null,	// attachment handler
				markupTemplater,
				formTemplater,
				displayTemplater,
				th,
				tc);
	}
	
	@Bean
	@ConditionalOnMissingBean
	public StorageIDResponseHandler teamsStorageIDResponseHandler(TeamsHistory th) {
		return new StorageIDResponseHandler(th);
	}
	
	
	public static enum StorageType { MEMORY, BLOB, DB };
	
	@Bean
	@ConditionalOnMissingBean
	public TeamsHistory teamsHistory(
			@Value("${teams.storage.type:blob}") StorageType st,
			@Value("${teams.storage.connection-string:}") String blobStorageConnectionString,
			@Value("${teams.storage.container:workflow-data}") String container) {
		
		if ((st == StorageType.MEMORY) || !StringUtils.hasText(blobStorageConnectionString)) {
			LOG.warn("Not configuring blob storage - using memory to store conversation state.  NOT FOR PRODUCTION");
			return new MemoryTeamsHistory();	
		} else if (st == StorageType.BLOB) {
			BlobServiceClient c = new BlobServiceClientBuilder()
					.connectionString(blobStorageConnectionString)
					.buildClient();
			
			return new AzureBlobStorageTeamsHistory(c, ejc, container);
		} else {
			throw new TeamsException("Couldn't configure TeamsHistory with "+st);
		}
	}

	@Bean
	public MicrosoftAppCredentials microsoftCredentials(@Value("${teams.app.tennantId}") String tennantId) {
		com.microsoft.bot.integration.Configuration conf = getConfiguration();
		MicrosoftAppCredentials mac = new MicrosoftAppCredentials(
				conf.getProperty(MicrosoftAppCredentials.MICROSOFTAPPID),
				conf.getProperty(MicrosoftAppCredentials.MICROSOFTAPPPASSWORD),
				tennantId);
		return mac;
	}
	
	@Bean 
	@ConditionalOnMissingBean
	public TeamsConversations teamsConversations(
			BotFrameworkAdapter bfa, 
			MicrosoftAppCredentials mac, 
			@Value("${teams.bot.id:}") String id) {
		ChannelAccount botAccount = new ChannelAccount(id);
		return new TeamsConversationsImpl(bfa, mac, botAccount);
	}

	@Bean
	@ConditionalOnMissingBean
	public TeamsFormConverter teamsFormConverter(TeamsConversations tc) {
		ObjectMapper om = new ObjectMapper();
		om.registerModule(new JavaTimeModule());
		om.registerModule(new TeamsFormDeserializerModule(tc));
		return new TeamsFormConverter(om);
	}
	
	@Bean
	@ConditionalOnMissingBean
	public MessageActivityHandler teamsMessageActivityHandler(
			List<ActionConsumer> messageConsumers, 
			TeamsHTMLParser parser, 
			FormValidationProcessor fvp, 
			TeamsConversations tc,
			TeamsHistory th,
			TeamsFormConverter fc) {
		return new MessageActivityHandler(messageConsumers, tc, th, parser, fc, fvp);
	}


    @Bean
    @ConditionalOnMissingBean
    public BotFrameworkHttpAdapter getBotFrameworkHttpAdaptor() {
    	AdapterWithErrorHandler out = new AdapterWithErrorHandler(getConfiguration());
    	return out;
    }
    
    @Override
	public com.microsoft.bot.integration.Configuration getConfiguration() {
    	return new com.microsoft.bot.integration.Configuration() {
			
			@Override
			public String getProperty(String key) {
				return ac.getEnvironment().getProperty("teams.bot."+key);
			}
			
			@Override
			public String[] getProperties(String key) {
				throw new UnsupportedOperationException("Couldn't getProperties for "+key);
			}
			
			@Override
			public Properties getProperties() {
				throw new UnsupportedOperationException();
			}
		};
	}

	@Bean
    @ConditionalOnMissingBean
    public BotController teamsBotController(MessageActivityHandler mah) {
    	return new BotController(getBotFrameworkHttpAdaptor(), mah);
    }
	
	@Bean
	@ConditionalOnMissingBean
	public AddressingChecker teamsAddressingChecker(TeamsConversations conv) {
		return new InRoomAddressingChecker(() -> {
			TurnContext tc = CurrentTurnContext.CURRENT_CONTEXT.get();
			ChannelAccount recipient = tc.getActivity().getRecipient();
			User u = conv.getUser(recipient);	
			return u;
		}, true);
	}

}