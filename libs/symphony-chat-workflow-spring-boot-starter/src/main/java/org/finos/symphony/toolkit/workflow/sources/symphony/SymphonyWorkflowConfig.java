package org.finos.symphony.toolkit.workflow.sources.symphony;

import java.util.List;

import org.finos.symphony.toolkit.spring.api.SymphonyApiConfig;
import org.finos.symphony.toolkit.stream.single.SharedStreamSingleBotConfig;
import org.finos.symphony.toolkit.workflow.actions.ActionConsumer;
import org.finos.symphony.toolkit.workflow.java.resolvers.FormDataArgumentWorkflowResolverFactory;
import org.finos.symphony.toolkit.workflow.java.resolvers.MessagePartWorkflowResolverFactory;
import org.finos.symphony.toolkit.workflow.sources.symphony.elements.ElementsConsumer;
import org.finos.symphony.toolkit.workflow.sources.symphony.elements.ElementsHandler;
import org.finos.symphony.toolkit.workflow.sources.symphony.elements.FormConverter;
import org.finos.symphony.toolkit.workflow.sources.symphony.elements.edit.EditActionElementsConsumer;
import org.finos.symphony.toolkit.workflow.sources.symphony.elements.edit.TableAddRow;
import org.finos.symphony.toolkit.workflow.sources.symphony.elements.edit.TableDeleteRows;
import org.finos.symphony.toolkit.workflow.sources.symphony.elements.edit.TableEditRow;
import org.finos.symphony.toolkit.workflow.sources.symphony.handlers.AttachmentHandler;
import org.finos.symphony.toolkit.workflow.sources.symphony.handlers.FormMessageMLConverter;
import org.finos.symphony.toolkit.workflow.sources.symphony.handlers.SymphonyResponseHandler;
import org.finos.symphony.toolkit.workflow.sources.symphony.handlers.freemarker.FreemarkerFormMessageMLConverter;
import org.finos.symphony.toolkit.workflow.sources.symphony.handlers.freemarker.TypeConverter;
import org.finos.symphony.toolkit.workflow.sources.symphony.history.SymphonyHistory;
import org.finos.symphony.toolkit.workflow.sources.symphony.history.SymphonyHistoryImpl;
import org.finos.symphony.toolkit.workflow.sources.symphony.json.EntityJsonConverter;
import org.finos.symphony.toolkit.workflow.sources.symphony.messages.PresentationMLHandler;
import org.finos.symphony.toolkit.workflow.sources.symphony.messages.SimpleMessageParser;
import org.finos.symphony.toolkit.workflow.sources.symphony.room.SymphonyRooms;
import org.finos.symphony.toolkit.workflow.sources.symphony.room.SymphonyRoomsImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.ResourceLoader;
import org.springframework.validation.Validator;

import com.symphony.api.agent.MessagesApi;
import com.symphony.api.id.SymphonyIdentity;
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
public class SymphonyWorkflowConfig {
	
	private static final Logger LOG = LoggerFactory.getLogger(SymphonyWorkflowConfig.class);
	
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
	@Lazy
	List<TypeConverter> converters;
	
	@Bean
	@ConditionalOnMissingBean
	public EditActionElementsConsumer editActionElementsConsumer() {
		return new EditActionElementsConsumer();
	}
	
	@Bean
	@ConditionalOnMissingBean
	public TableAddRow tableAddRow() {
		return new TableAddRow();
	}
	
	@Bean
	@ConditionalOnMissingBean
	public TableDeleteRows tableDeleteRows() {
		return new TableDeleteRows();
	}
	
	@Bean
	@ConditionalOnMissingBean
	public TableEditRow tableEditRow() {
		return new TableEditRow();
	}
	
	@Bean
	@ConditionalOnMissingBean
	public FormDataArgumentWorkflowResolverFactory elementsArgumentWorkflowResolverFactory() {
		return new FormDataArgumentWorkflowResolverFactory();
	}
	
	@Bean
	@ConditionalOnMissingBean
	public MessagePartWorkflowResolverFactory messagePartWorkflowResolverFactory() {
		return new MessagePartWorkflowResolverFactory();
	}


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
		LOG.info("Setting up Freemarker formMessageMLConverter with {} converters", converters.size());
		return new FreemarkerFormMessageMLConverter(resourceLoader, converters);
	}
	
	@Bean
	@ConditionalOnMissingBean
	public SymphonyHistory symphonyHistory() {
		return new SymphonyHistoryImpl(entityJsonConverter(), messagesApi, symphonyRooms());
	}
	
	@Bean 
	@ConditionalOnMissingBean
	public SymphonyRooms symphonyRooms() {
		return new SymphonyRoomsImpl(roomMembershipApi, streamsApi, usersApi);
	}
	
	
	@Bean
	@ConditionalOnMissingBean
	public EntityJsonConverter entityJsonConverter() {
		return new EntityJsonConverter();
	}
	
	@Bean
	@ConditionalOnMissingBean
	public PresentationMLHandler presentationMLHandler(List<ActionConsumer> messageConsumers) {
		return new PresentationMLHandler(botIdentity, usersApi, simpleMessageParser(), entityJsonConverter(), messageConsumers, symphonyResponseHandler(), symphonyRooms());
	}
	
	@Bean
	@ConditionalOnMissingBean
	public ElementsHandler elementsHandler(List<ElementsConsumer> elementsConsumers) {
		return new ElementsHandler(messagesApi, entityJsonConverter(), new FormConverter(symphonyRooms()), elementsConsumers, symphonyResponseHandler(), symphonyRooms(), validator);
	}
	
	@Bean
	@ConditionalOnMissingBean
	public SymphonyBot symphonyBot(List<SymphonyEventHandler> eventHandlers) {
		return new SymphonyBot(botIdentity, eventHandlers);
	}

}