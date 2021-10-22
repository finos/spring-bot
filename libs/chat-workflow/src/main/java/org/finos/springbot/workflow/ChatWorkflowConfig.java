package org.finos.springbot.workflow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.finos.springbot.workflow.actions.consumers.AddressingChecker;
import org.finos.springbot.workflow.actions.consumers.ChatWorkflowErrorHandler;
import org.finos.springbot.workflow.actions.form.FormEditConfig;
import org.finos.springbot.workflow.conversations.AllConversations;
import org.finos.springbot.workflow.help.HelpController;
import org.finos.springbot.workflow.history.AllHistory;
import org.finos.springbot.workflow.java.converters.CollectionResponseConverter;
import org.finos.springbot.workflow.java.converters.ContentResponseConverter;
import org.finos.springbot.workflow.java.converters.ResponseConverter;
import org.finos.springbot.workflow.java.converters.ResponseConverters;
import org.finos.springbot.workflow.java.converters.WorkResponseConverter;
import org.finos.springbot.workflow.java.mapping.ChatButtonChatHandlerMapping;
import org.finos.springbot.workflow.java.mapping.ChatHandlerExecutor;
import org.finos.springbot.workflow.java.mapping.ChatHandlerMapping;
import org.finos.springbot.workflow.java.mapping.ChatHandlerMappingActionConsumer;
import org.finos.springbot.workflow.java.mapping.ChatRequestChatHandlerMapping;
import org.finos.springbot.workflow.java.resolvers.ResolverConfig;
import org.finos.springbot.workflow.java.resolvers.WorkflowResolversFactory;
import org.finos.springbot.workflow.response.Response;
import org.finos.springbot.workflow.response.handlers.ButtonsResponseHandler;
import org.finos.springbot.workflow.response.handlers.ChatListResponseHandler;
import org.finos.springbot.workflow.response.handlers.ResponseHandler;
import org.finos.springbot.workflow.response.handlers.ResponseHandlers;
import org.finos.springbot.workflow.response.handlers.UserListResponseHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.OrderComparator;
import org.springframework.util.ErrorHandler;

@Configuration
@Import(value =  { ResolverConfig.class, FormEditConfig.class })
public class ChatWorkflowConfig {

	@Bean
	@ConditionalOnMissingBean
	@Lazy
	public HelpController helpConsumer() {
		return new HelpController();
	}

	@Bean
	@ConditionalOnMissingBean
	public WorkResponseConverter workResponseConverter(ResponseHandlers rh) {
		return new WorkResponseConverter(rh);
	} 
	
	@Bean
	@ConditionalOnMissingBean
	public ContentResponseConverter contentResponseConverter(ResponseHandlers rh) {
		return new ContentResponseConverter(rh);
	} 
	
	@Bean
	@ConditionalOnMissingBean
	public CollectionResponseConverter collectionResponseConverter(ResponseHandlers rh) {
		return new CollectionResponseConverter(rh);
	} 
	
	@Bean
	@ConditionalOnMissingBean
	public ButtonsResponseHandler buttonsResponseHandler() {
		return new ButtonsResponseHandler();
	} 
	
	@Bean
	@ConditionalOnMissingBean
	public ChatListResponseHandler chatListResponseHandler() {
		return new ChatListResponseHandler(allConversations());
	} 
	
	@Bean
	@ConditionalOnMissingBean
	public UserListResponseHandler userListResponseHandler() {
		return new UserListResponseHandler(allConversations());
	} 
	
	@Bean
	@ConditionalOnMissingBean
	public ChatWorkflowErrorHandler chatWorkflowErrorHandler(ResponseHandlers rh) {
		return new ChatWorkflowErrorHandler(rh, "default-error");
	} 

	@Bean
	@ConditionalOnMissingBean
	public ChatButtonChatHandlerMapping buttonHandlerMapping(WorkflowResolversFactory wrf,
			ResponseConverters converters, AllConversations conversations) {
		return new ChatButtonChatHandlerMapping(wrf, converters, conversations);
	}
	
	@Bean
	@ConditionalOnMissingBean
	public ChatRequestChatHandlerMapping chatHandlerMapping(WorkflowResolversFactory wrf, 
			ResponseConverters converters, AllConversations conversations) {
		return new ChatRequestChatHandlerMapping(wrf, converters, conversations);
	}
	
	@Bean
	@ConditionalOnMissingBean
	public ChatHandlerMappingActionConsumer methodCallMessageConsumer(List<ChatHandlerMapping<?>> chatHandlerMappings, ErrorHandler eh, List<AddressingChecker> ac) {
		return new ChatHandlerMappingActionConsumer(chatHandlerMappings, eh, ac);
	}
	
	@Bean
	@ConditionalOnMissingBean
	public AllConversations allConversations() {
		return new AllConversations();
	}
	
	@Bean
	@ConditionalOnMissingBean
	public AllHistory allHistory() {
		return new AllHistory();
	}

	@Bean
	@ConditionalOnMissingBean
	public ResponseHandlers responseHandlers(List<ResponseHandler> rh) {
		List<ResponseHandler> sorted = new ArrayList<>(rh);
		Collections.sort(sorted, OrderComparator.INSTANCE);
		return new ResponseHandlers() {
			
			@Override
			public void accept(Response t) {
				sorted.forEach(rh -> rh.accept(t));
			}
		};
	}
	
	@Bean
	@ConditionalOnMissingBean
	public ResponseConverters responseConverters(List<ResponseConverter> rh) {
		List<ResponseConverter> sorted = new ArrayList<>(rh);
		Collections.sort(sorted, OrderComparator.INSTANCE);
		return new ResponseConverters() {
			
			@Override
			public void accept(Object t, ChatHandlerExecutor che) {
				sorted.forEach(rh -> rh.accept(t, che));
			}
		};
	}
}
