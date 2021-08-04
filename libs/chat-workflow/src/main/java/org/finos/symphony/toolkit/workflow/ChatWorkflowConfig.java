package org.finos.symphony.toolkit.workflow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.finos.symphony.toolkit.workflow.actions.consumers.AddressingChecker;
import org.finos.symphony.toolkit.workflow.actions.consumers.ChatWorkflowErrorHandler;
import org.finos.symphony.toolkit.workflow.actions.form.FormEditConfig;
import org.finos.symphony.toolkit.workflow.help.HelpController;
import org.finos.symphony.toolkit.workflow.java.converters.ResponseConverter;
import org.finos.symphony.toolkit.workflow.java.converters.WorkResponseConverter;
import org.finos.symphony.toolkit.workflow.java.mapping.ButtonRequestChatHandlerMapping;
import org.finos.symphony.toolkit.workflow.java.mapping.ChatHandlerMapping;
import org.finos.symphony.toolkit.workflow.java.mapping.ChatHandlerMappingActionConsumer;
import org.finos.symphony.toolkit.workflow.java.mapping.ChatRequestChatHandlerMapping;
import org.finos.symphony.toolkit.workflow.java.resolvers.ResolverConfig;
import org.finos.symphony.toolkit.workflow.java.resolvers.WorkflowResolversFactory;
import org.finos.symphony.toolkit.workflow.response.Response;
import org.finos.symphony.toolkit.workflow.response.handlers.ButtonsResponseHandler;
import org.finos.symphony.toolkit.workflow.response.handlers.ResponseHandler;
import org.finos.symphony.toolkit.workflow.response.handlers.ResponseHandlers;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.OrderComparator;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.annotation.Order;
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
	public ResponseConverter formResponseConverter() {
		return new WorkResponseConverter();
	} 
	
	@Bean
	@ConditionalOnMissingBean
	public ButtonsResponseHandler buttonsResponseHandler() {
		return new ButtonsResponseHandler();
	} 
	
	@Bean
	@ConditionalOnMissingBean
	public ChatWorkflowErrorHandler chatWorkflowErrorHandler(ResponseHandlers rh) {
		return new ChatWorkflowErrorHandler(rh, "default-error");
	} 

	@Bean
	@ConditionalOnMissingBean
	public ButtonRequestChatHandlerMapping buttonHandlerMapping(WorkflowResolversFactory wrf, ResponseHandlers rh,
			List<ResponseConverter> converters) {
		return new ButtonRequestChatHandlerMapping(wrf, rh, converters);
	}
	
	@Bean
	@ConditionalOnMissingBean
	public ChatRequestChatHandlerMapping chatHandlerMapping(WorkflowResolversFactory wrf, ResponseHandlers rh,
			List<ResponseConverter> converters) {
		return new ChatRequestChatHandlerMapping(wrf, rh, converters);
	}
	
	@Bean
	@ConditionalOnMissingBean
	public ChatHandlerMappingActionConsumer methodCallMessageConsumer(List<ChatHandlerMapping<?>> chatHandlerMappings, ErrorHandler eh, AddressingChecker ac) {
		return new ChatHandlerMappingActionConsumer(chatHandlerMappings, eh, ac);
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
}
