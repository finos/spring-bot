package org.finos.symphony.toolkit.workflow.java.perform;

import org.finos.symphony.toolkit.workflow.CommandPerformer;
import org.finos.symphony.toolkit.workflow.java.resolvers.WorkflowResolversFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
