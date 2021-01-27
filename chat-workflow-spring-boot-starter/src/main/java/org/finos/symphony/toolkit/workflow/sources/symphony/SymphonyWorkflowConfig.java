package org.finos.symphony.toolkit.workflow.sources.symphony;

import java.util.List;
import java.util.Optional;

import org.finos.symphony.toolkit.spring.api.SymphonyApiConfig;
import org.finos.symphony.toolkit.stream.single.SharedStreamSingleBotConfig;
import org.finos.symphony.toolkit.workflow.CommandPerformer;
import org.finos.symphony.toolkit.workflow.Workflow;
import org.finos.symphony.toolkit.workflow.content.Addressable;
import org.finos.symphony.toolkit.workflow.history.History;
import org.finos.symphony.toolkit.workflow.java.resolvers.WorkflowResolver;
import org.finos.symphony.toolkit.workflow.java.resolvers.WorkflowResolverFactory;
import org.finos.symphony.toolkit.workflow.sources.symphony.elements.ElementsArgumentWorkflowResolverFactory;
import org.finos.symphony.toolkit.workflow.sources.symphony.elements.ElementsConsumer;
import org.finos.symphony.toolkit.workflow.sources.symphony.elements.ElementsHandler;
import org.finos.symphony.toolkit.workflow.sources.symphony.elements.FormConverter;
import org.finos.symphony.toolkit.workflow.sources.symphony.elements.MethodCallElementsConsumer;
import org.finos.symphony.toolkit.workflow.sources.symphony.elements.edit.EditActionElementsConsumer;
import org.finos.symphony.toolkit.workflow.sources.symphony.elements.edit.TableAddRow;
import org.finos.symphony.toolkit.workflow.sources.symphony.elements.edit.TableDeleteRows;
import org.finos.symphony.toolkit.workflow.sources.symphony.elements.edit.TableEditRow;
import org.finos.symphony.toolkit.workflow.sources.symphony.handlers.AttachmentHandler;
import org.finos.symphony.toolkit.workflow.sources.symphony.handlers.EntityJsonConverter;
import org.finos.symphony.toolkit.workflow.sources.symphony.handlers.FormMessageMLConverter;
import org.finos.symphony.toolkit.workflow.sources.symphony.handlers.SymphonyResponseHandler;
import org.finos.symphony.toolkit.workflow.sources.symphony.handlers.freemarker.FieldConverter;
import org.finos.symphony.toolkit.workflow.sources.symphony.handlers.freemarker.FreemarkerFormMessageMLConverter;
import org.finos.symphony.toolkit.workflow.sources.symphony.history.MessageHistory;
import org.finos.symphony.toolkit.workflow.sources.symphony.messages.HelpMessageConsumer;
import org.finos.symphony.toolkit.workflow.sources.symphony.messages.MessagePartWorkflowResolverFactory;
import org.finos.symphony.toolkit.workflow.sources.symphony.messages.MethodCallMessageConsumer;
import org.finos.symphony.toolkit.workflow.sources.symphony.messages.PresentationMLHandler;
import org.finos.symphony.toolkit.workflow.sources.symphony.messages.SimpleMessageConsumer;
import org.finos.symphony.toolkit.workflow.sources.symphony.messages.SimpleMessageParser;
import org.finos.symphony.toolkit.workflow.sources.symphony.room.SymphonyRooms;
import org.finos.symphony.toolkit.workflow.sources.symphony.room.SymphonyRoomsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
	Workflow wf;
	
	@Autowired
	CommandPerformer cp;
	
	@Autowired
	List<FieldConverter> converters;
	
	@Bean
	@ConditionalOnMissingBean
	public HelpMessageConsumer helpConsumer() {
		return new HelpMessageConsumer();
	}
	
	@Bean
	@ConditionalOnMissingBean
	public MethodCallMessageConsumer mcConsumer() {
		return new MethodCallMessageConsumer(cp);
	}
	
	@Bean
	@ConditionalOnMissingBean
	public MethodCallElementsConsumer elementsMethodCallConsumer() {
		return new MethodCallElementsConsumer(cp);
	}

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
	public ElementsArgumentWorkflowResolverFactory elementsArgumentWorkflowResolverFactory() {
		return new ElementsArgumentWorkflowResolverFactory();
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
		return new FreemarkerFormMessageMLConverter(resourceLoader, converters);
	}
	
	@Bean
	@ConditionalOnMissingBean
	public History symphonyHistory() {
		return new MessageHistory(wf, entityJsonConverter(), messagesApi, symphonyRooms());
	}
	
	@Bean 
	@ConditionalOnMissingBean
	public SymphonyRooms symphonyRooms() {
		return new SymphonyRoomsImpl(wf, roomMembershipApi, streamsApi, usersApi);
	}
	
	
	@Bean
	@ConditionalOnMissingBean
	public EntityJsonConverter entityJsonConverter() {
		return new EntityJsonConverter(wf);
	}
	
	@Bean
	@ConditionalOnMissingBean
	public PresentationMLHandler presentationMLHandler(List<SimpleMessageConsumer> messageConsumers) {
		return new PresentationMLHandler(wf, botIdentity, usersApi, simpleMessageParser(), entityJsonConverter(), messageConsumers, symphonyResponseHandler(), symphonyRooms());
	}
	
	@Bean
	@ConditionalOnMissingBean
	public ElementsHandler elementsHandler(List<ElementsConsumer> elementsConsumers) {
		return new ElementsHandler(wf, messagesApi, entityJsonConverter(), new FormConverter(symphonyRooms()), elementsConsumers, symphonyResponseHandler(), symphonyRooms(), validator);
	}
	

	@Bean
	@ConditionalOnMissingBean
	public SymphonyBot symphonyBot(List<SymphonyEventHandler> eventHandlers) {
		return new SymphonyBot(botIdentity, eventHandlers);
	}
	
	/**
	 * Allows resolution of "this" or a parameter matching something in the workflow.
	 */
	@SuppressWarnings("unchecked")
	@Bean
	public WorkflowResolverFactory symphonyLastMessageResolver(History sh, EntityJsonConverter ejc) {
		return action -> {
			return new WorkflowResolver() {
				
				@Override
				public Optional<Object> resolve(Class<?> cl, Addressable a, boolean isTarget) {
					if (!isTarget) {
						return Optional.empty();
					}
					Object oo = ejc.readWorkflow(action.getData());
					if ((oo != null) && (cl.isAssignableFrom(oo.getClass()))) {
						return Optional.of(oo);
					} else if (wf.getDataTypes().contains(cl)) {
						return (Optional<Object>) sh.getLastFromHistory(cl, a);	
					} else {
						return Optional.empty();
					}
				}
				
				@Override
				public boolean canResolve(Class<?> c) {
					return wf.getDataTypes().contains(c);
				}
			};
		};
	}
	
}