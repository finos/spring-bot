package org.finos.symphony.toolkit.workflow;

import java.util.List;

import org.finos.symphony.toolkit.workflow.help.HelpController;
import org.finos.symphony.toolkit.workflow.java.converters.FormResponseConverter;
import org.finos.symphony.toolkit.workflow.java.converters.ResponseConverter;
import org.finos.symphony.toolkit.workflow.java.mapping.ExposedHandlerMapping;
import org.finos.symphony.toolkit.workflow.java.resolvers.AddressableWorkflowResolverFactory;
import org.finos.symphony.toolkit.workflow.java.resolvers.ChatVariableWorkflowResolverFactory;
import org.finos.symphony.toolkit.workflow.java.resolvers.WorkflowResolversFactory;
import org.finos.symphony.toolkit.workflow.message.MessagePartWorkflowResolverFactory;
import org.finos.symphony.toolkit.workflow.response.ResponseHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatWorkflowConfig {

	@Bean
	@ConditionalOnMissingBean
	public HelpController helpConsumer() {
		return new HelpController();
	}

	@Bean
	@ConditionalOnMissingBean
	public ResponseConverter formResponseConverter() {
		return new FormResponseConverter();
	} 

	@Bean
	@ConditionalOnMissingBean
	public ExposedHandlerMapping handlerMapping(WorkflowResolversFactory wrf, ResponseHandler rh,
			List<ResponseConverter> converters) {
		return new ExposedHandlerMapping(wrf, rh, converters);
	}

	@Bean
	@ConditionalOnMissingBean
	public MessagePartWorkflowResolverFactory messagePartWorkflowResolverFactory() {
		return new MessagePartWorkflowResolverFactory();
	}

	@Bean
	@ConditionalOnMissingBean
	public ChatVariableWorkflowResolverFactory chatVariableWorkflowResolverFactory() {
		return new ChatVariableWorkflowResolverFactory();
	}

	@Bean
	@ConditionalOnMissingBean
	public AddressableWorkflowResolverFactory addressableWorkflowResolverFactory() {
		return new AddressableWorkflowResolverFactory();
	}


}
