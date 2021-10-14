package org.finos.springbot.sources.teams;

import java.util.List;
import java.util.Properties;

import org.finos.springbot.sources.teams.conversations.TeamsConversations;
import org.finos.springbot.sources.teams.conversations.TeamsConversationsImpl;
import org.finos.springbot.sources.teams.handlers.TeamsResponseHandler;
import org.finos.springbot.sources.teams.messages.MessageActivityHandler;
import org.finos.springbot.sources.teams.messages.TeamsHTMLParser;
import org.finos.springbot.sources.teams.messages.TeamsXMLWriter;
import org.finos.springbot.sources.teams.turns.CurrentTurnContext;
import org.finos.symphony.toolkit.workflow.ChatWorkflowConfig;
import org.finos.symphony.toolkit.workflow.actions.consumers.ActionConsumer;
import org.finos.symphony.toolkit.workflow.actions.consumers.AddressingChecker;
import org.finos.symphony.toolkit.workflow.actions.consumers.InRoomAddressingChecker;
import org.finos.symphony.toolkit.workflow.content.BlockQuote;
import org.finos.symphony.toolkit.workflow.content.Message;
import org.finos.symphony.toolkit.workflow.content.OrderedList;
import org.finos.symphony.toolkit.workflow.content.Paragraph;
import org.finos.symphony.toolkit.workflow.content.Table;
import org.finos.symphony.toolkit.workflow.content.UnorderedList;
import org.finos.symphony.toolkit.workflow.content.User;
import org.finos.symphony.toolkit.workflow.content.Word;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ResourceLoader;
import org.springframework.validation.Validator;

import com.microsoft.bot.builder.TurnContext;
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
@Import({ChatWorkflowConfig.class})
public class TeamsWorkflowConfig extends BotDependencyConfiguration {
	
	private static final Logger LOG = LoggerFactory.getLogger(TeamsWorkflowConfig.class);
	
	@Autowired
	Validator validator;
	
	@Autowired
	ResourceLoader resourceLoader;
	
	@Autowired
	ApplicationContext ac;
	
	@Bean
	@ConditionalOnMissingBean
	public TeamsXMLWriter teamsHTMLWriter() {
		TeamsXMLWriter out = new TeamsXMLWriter();
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
	public TeamsResponseHandler teamsResponseHandler() {
		return new TeamsResponseHandler(
				teamsHTMLWriter(), 
				null, 
				null, 
				resourceLoader);
	}
//	
//	@Bean
//	@ConditionalOnMissingBean
//	public FormMessageMLConverter formMessageMLConverter() {
//		LOG.info("Setting up Freemarker formMessageMLConverter with {} converters", converters.size());
//		return new FreemarkerFormMessageMLConverter(converters);
//	}
//	
//	@Bean
//	@ConditionalOnMissingBean
//	public SymphonyHistory symphonyHistory() {
//		return new SymphonyHistoryImpl(entityJsonConverter(), messagesApi, streamsApi, usersApi);
//	}
	
	@Bean 
	@ConditionalOnMissingBean
	public TeamsConversations teamsConversations() {
		return new TeamsConversationsImpl();
	}
	
	@Bean
	@ConditionalOnMissingBean
	public MessageActivityHandler messsageActivityHandler(List<ActionConsumer> messageConsumers, TeamsHTMLParser parser) {
		return new MessageActivityHandler(messageConsumers, teamsConversations(), parser);
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
    public BotController botController(MessageActivityHandler mah) {
    	return new BotController(getBotFrameworkHttpAdaptor(), mah);
    }

//	@Bean
//	@ConditionalOnMissingBean
//	public FormConverter formConverter() {
//		return new FormConverter(symphonyRooms());
//	}
//	
	@Bean
	@ConditionalOnMissingBean
	public AddressingChecker teamsAddressingChecker(TeamsConversations conv) {
		return new InRoomAddressingChecker(() -> {
			TurnContext tc = CurrentTurnContext.CURRENT_CONTEXT.get();
			User u = conv.getUser(tc.getActivity().getRecipient());	
			return u;
		}, true);
	}
//	
//	@Bean
//	@ConditionalOnMissingBean
//	public ElementsHandler elementsHandler(List<ActionConsumer> elementsConsumers) {
//		return new ElementsHandler(messagesApi, entityJsonConverter(), new FormConverter(symphonyRooms()), elementsConsumers, symphonyResponseHandler(), symphonyRooms(), validator);
//	}

}