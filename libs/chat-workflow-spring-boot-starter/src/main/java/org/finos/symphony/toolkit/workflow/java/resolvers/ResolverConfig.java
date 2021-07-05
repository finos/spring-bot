package org.finos.symphony.toolkit.workflow.java.resolvers;

import java.util.Optional;

import org.finos.symphony.toolkit.workflow.history.History;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.MethodParameter;

@Configuration
public class ResolverConfig {

	@Autowired
	BeanFactory context;
	
	/**
	 * Allows you to put any beans you like as parameters of the workflow
	 */
	@Bean
	public WorkflowResolverFactory springBeanResolver() {
		return action -> {
			return new WorkflowResolver() {

				@Override
				public boolean canResolve(MethodParameter mp) {
					try {
						Class<?> c = mp.getParameterType();
						context.getBean(c);
						return true;
					} catch (NoSuchBeanDefinitionException e) {
						return false;
					}
				}

				@Override
				public Optional<Object> resolve(MethodParameter mp) {
					try {
					
						Class<?> c = mp.getParameterType();
						Object bean = context.getBean(c);
						return Optional.of(bean);
					} catch (NoSuchBeanDefinitionException e) {
						return Optional.empty();
					}
				}
			};
		};
	}

	@Bean
	@ConditionalOnMissingBean
	public MessagePartWorkflowResolverFactory messagePartWorkflowResolverFactory() {
		return new MessagePartWorkflowResolverFactory();
	}
	
	@Bean
	@ConditionalOnMissingBean
	public FormDataArgumentWorkflowResolverFactory formDataArgumentWorkflowResolverFactory() {
		return new FormDataArgumentWorkflowResolverFactory();
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

	/**
	 * Allows you to pull back previous exchanges in the history to use as parameters
	 */
	@Bean
	public MessageHistoryWorkflowResolverFactory historyResolver(@Lazy History hist) {
		return new MessageHistoryWorkflowResolverFactory(hist);
	}
	
	@Bean
	public WorkflowResolversFactory workflowResolversFactory() {
		return new WorkflowResolversFactory();
	}
	
}
