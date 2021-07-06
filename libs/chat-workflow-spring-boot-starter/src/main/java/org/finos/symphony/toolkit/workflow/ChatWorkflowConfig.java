package org.finos.symphony.toolkit.workflow;

import java.util.List;

import org.finos.symphony.toolkit.workflow.annotations.Exposed;
import org.finos.symphony.toolkit.workflow.help.HelpController;
import org.finos.symphony.toolkit.workflow.java.converters.FormResponseConverter;
import org.finos.symphony.toolkit.workflow.java.converters.ResponseConverter;
import org.finos.symphony.toolkit.workflow.java.mapping.ChatHandlerMapping;
import org.finos.symphony.toolkit.workflow.java.mapping.ExposedHandlerMapping;
import org.finos.symphony.toolkit.workflow.java.resolvers.ResolverConfig;
import org.finos.symphony.toolkit.workflow.java.resolvers.WorkflowResolversFactory;
import org.finos.symphony.toolkit.workflow.message.ChatWorkflowErrorHandler;
import org.finos.symphony.toolkit.workflow.message.MethodCallMessageConsumer;
import org.finos.symphony.toolkit.workflow.response.handlers.ResponseHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.util.ErrorHandler;

@Configuration
@Import(ResolverConfig.class)
public class ChatWorkflowConfig {

	@Bean
	@ConditionalOnMissingBean
	public HelpController helpConsumer(List<ChatHandlerMapping<Exposed>> chatHandlerMappings) {
		return new HelpController(chatHandlerMappings);
	}

	@Bean
	@ConditionalOnMissingBean
	public ResponseConverter formResponseConverter() {
		return new FormResponseConverter();
	} 
	
	@Bean
	@ConditionalOnMissingBean
	public ChatWorkflowErrorHandler chatWorkflowErrorHandler(ResponseHandler rh) {
		return new ChatWorkflowErrorHandler(rh, "default-error");
	} 

	@Bean
	@ConditionalOnMissingBean
	public ExposedHandlerMapping handlerMapping(WorkflowResolversFactory wrf, ResponseHandler rh,
			List<ResponseConverter> converters) {
		return new ExposedHandlerMapping(wrf, rh, converters);
	}
	
	@Bean
	@ConditionalOnMissingBean
	public MethodCallMessageConsumer methodCallMessageConsumer(List<ChatHandlerMapping<?>> chatHandlerMappings, ErrorHandler eh) {
		return new MethodCallMessageConsumer(chatHandlerMappings, eh);
	}

}
