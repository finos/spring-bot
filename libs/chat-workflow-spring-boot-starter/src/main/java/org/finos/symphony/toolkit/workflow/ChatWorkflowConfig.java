package org.finos.symphony.toolkit.workflow;

import org.finos.symphony.toolkit.workflow.help.HelpController;
import org.finos.symphony.toolkit.workflow.java.converters.FormResponseConverter;
import org.finos.symphony.toolkit.workflow.java.converters.ResponseConverter;
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
	ResponseConverter formResponseConverter() {
		return new FormResponseConverter();
	}
	
}
