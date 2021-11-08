package org.finos.springbot.teams;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import org.finos.springbot.ChatWorkflowConfig;
import org.finos.springbot.teams.conversations.TeamsConversations;
import org.finos.springbot.teams.conversations.TeamsConversationsImpl;
import org.finos.springbot.teams.form.TeamsFormConverter;
import org.finos.springbot.teams.form.TeamsFormDeserializerModule;
import org.finos.springbot.teams.handlers.TeamsResponseHandler;
import org.finos.springbot.teams.handlers.TeamsTemplateProvider;
import org.finos.springbot.teams.messages.MessageActivityHandler;
import org.finos.springbot.teams.messages.TeamsHTMLParser;
import org.finos.springbot.teams.templating.AdaptiveCardConverterConfig;
import org.finos.springbot.teams.templating.AdaptiveCardTemplater;
import org.finos.springbot.teams.turns.CurrentTurnContext;
import org.finos.springbot.workflow.actions.consumers.ActionConsumer;
import org.finos.springbot.workflow.actions.consumers.AddressingChecker;
import org.finos.springbot.workflow.actions.consumers.InRoomAddressingChecker;
import org.finos.springbot.workflow.content.BlockQuote;
import org.finos.springbot.workflow.content.Message;
import org.finos.springbot.workflow.content.OrderedList;
import org.finos.springbot.workflow.content.Paragraph;
import org.finos.springbot.workflow.content.Table;
import org.finos.springbot.workflow.content.UnorderedList;
import org.finos.springbot.workflow.content.User;
import org.finos.springbot.workflow.content.Word;
import org.finos.springbot.workflow.content.serialization.MarkupWriter;
import org.finos.springbot.workflow.form.FormConverter;
import org.finos.springbot.workflow.form.FormValidationProcessor;
import org.finos.springbot.workflow.response.templating.SimpleMessageMarkupTemplateProvider;
import org.finos.springbot.workflow.templating.Rendering;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ResourceLoader;
import org.springframework.validation.Validator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.microsoft.bot.builder.BotFrameworkAdapter;
import com.microsoft.bot.builder.TurnContext;
import com.microsoft.bot.connector.authentication.MicrosoftAppCredentials;
import com.microsoft.bot.connector.rest.RestConnectorClient;
import com.microsoft.bot.integration.AdapterWithErrorHandler;
import com.microsoft.bot.integration.BotFrameworkHttpAdapter;
import com.microsoft.bot.integration.spring.BotController;
import com.microsoft.bot.integration.spring.BotDependencyConfiguration;

/**
 * Symphony beans needing the workflow bean to be defined.
 * 
 * @author moffrob
 *
 */
@Configuration
@Import({ChatWorkflowConfig.class, AdaptiveCardConverterConfig.class})
@ConditionalOnProperty("MicrosoftAppId")
public class TeamsWorkflowConfig extends BotDependencyConfiguration {
	
	private static final Logger LOG = LoggerFactory.getLogger(TeamsWorkflowConfig.class);
	
	@Autowired
	Validator validator;
	
	@Autowired
	ResourceLoader resourceLoader;
	
	@Autowired
	ApplicationContext ac;
	
	@Autowired
	Rendering<JsonNode> r;
	
	@Bean
	@ConditionalOnMissingBean
	public MarkupWriter teamsHTMLWriter() {
		MarkupWriter out = new MarkupWriter();
		out.add(Message.class, out.new OrderedTagWriter("div"));
		out.add(Paragraph.class, out.new OrderedTagWriter("p"));
		out.add(OrderedList.class, out.new OrderedTagWriter("ol", out.new OrderedTagWriter("li")));
		out.add(UnorderedList.class, out.new OrderedTagWriter("ul", out.new OrderedTagWriter("li")));
		out.add(BlockQuote.class, out.new SimpleTagWriter("code"));
		out.add(Word.class, out.new PlainWriter());
		out.add(Table.class, out.new TableWriter());
		out.add(BlockQuote.class, out.new SimpleTagWriter("blockquote"));
		// tags, images, links etc.
		
		return out;
	}
	
	@Bean 
	@ConditionalOnMissingBean
	public SimpleMessageMarkupTemplateProvider teamsMarkupTemplater(
			@Value("${teams.templates.prefix:classpath:/templates/teams/}") String prefix,
			@Value("${teams.templates.suffix:.html}") String suffix,
			MarkupWriter converter) {
		return new SimpleMessageMarkupTemplateProvider(prefix, suffix, resourceLoader, converter);
	}
	
	@Bean
	@ConditionalOnMissingBean
	public TeamsTemplateProvider teamsWorkTemplater(
			@Value("${symphony.templates.prefix:classpath:/templates/teams/}") String prefix,
			@Value("${symphony.templates.suffix:.json}") String suffix,
			AdaptiveCardTemplater formConverter) throws IOException {
		return new TeamsTemplateProvider(prefix, suffix, resourceLoader, formConverter);
	}
	
	@Bean
	@ConditionalOnMissingBean
	public TeamsResponseHandler teamsResponseHandler(
			SimpleMessageMarkupTemplateProvider markupTemplater,
			TeamsTemplateProvider workTemplater) {
		return new TeamsResponseHandler(
				null,
				markupTemplater,
				workTemplater);
	}
	
//	
//	@Bean
//	@ConditionalOnMissingBean
//	public SymphonyHistory symphonyHistory() {
//		return new SymphonyHistoryImpl(entityJsonConverter(), messagesApi, streamsApi, usersApi);
//	}
	
	@Bean 
	@ConditionalOnMissingBean
	public TeamsConversations teamsConversations(BotFrameworkAdapter bfa) {
		com.microsoft.bot.integration.Configuration conf = getConfiguration();
		MicrosoftAppCredentials mac = new MicrosoftAppCredentials(
			conf.getProperty(MicrosoftAppCredentials.MICROSOFTAPPID), 
			conf.getProperty(MicrosoftAppCredentials.MICROSOFTAPPPASSWORD),
			"2f758e82-b31e-4a99-a9dd-e4d4abe351db");
		mac.setChannelAuthTenant(null);
		RestConnectorClient rcc = new RestConnectorClient(mac);
		return new TeamsConversationsImpl(bfa, rcc);
	}

	@Bean
	@ConditionalOnMissingBean
	public TeamsFormConverter teamsFormConverter() {
		ObjectMapper om = new ObjectMapper();
		om.registerModule(new JavaTimeModule());
		om.registerModule(new TeamsFormDeserializerModule());
		return new TeamsFormConverter(om);
	}
	
	@Bean
	@ConditionalOnMissingBean
	public MessageActivityHandler teamsMessageActivityHandler(
			List<ActionConsumer> messageConsumers, 
			TeamsHTMLParser parser, 
			FormValidationProcessor fvp, 
			TeamsConversations tc) {
		return new MessageActivityHandler(messageConsumers, tc, parser, teamsFormConverter(), fvp);
	}
	
	@Bean
	@ConditionalOnMissingBean
	public TeamsHTMLParser teamsHTMLParser() {
		return new TeamsHTMLParser();
	}
	

    @Bean
    @ConditionalOnMissingBean
    public BotFrameworkHttpAdapter getBotFrameworkHttpAdaptor() {
        return new AdapterWithErrorHandler(getConfiguration());
    }
    
    @Override
	public com.microsoft.bot.integration.Configuration getConfiguration() {
    	return new com.microsoft.bot.integration.Configuration() {
			
			@Override
			public String getProperty(String key) {
				return ac.getEnvironment().getProperty(key);
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
			User u = conv.getUser(tc.getActivity().getRecipient());	
			return u;
		}, true);
		
//		return a -> a;
	}

}