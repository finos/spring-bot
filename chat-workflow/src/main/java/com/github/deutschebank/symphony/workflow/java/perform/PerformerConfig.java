package com.github.deutschebank.symphony.workflow.java.perform;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.deutschebank.symphony.workflow.CommandPerformer;
import com.github.deutschebank.symphony.workflow.java.resolvers.WorkflowResolversFactory;

@Configuration
public class PerformerConfig {

	@Autowired
	WorkflowResolversFactory workflowResolversFactory;
	
	@Bean
	@ConditionalOnMissingBean
	public CommandPerformer commandPerformer() {
		return new MethodCallCommandPerformer(workflowResolversFactory);
	}
}
