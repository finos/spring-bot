package org.finos.symphony.toolkit.workflow.java.resolvers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.finos.symphony.toolkit.workflow.content.BlockQuote;
import org.finos.symphony.toolkit.workflow.content.Content;
import org.finos.symphony.toolkit.workflow.content.Message;
import org.finos.symphony.toolkit.workflow.content.OrderedList;
import org.finos.symphony.toolkit.workflow.content.Paragraph;
import org.finos.symphony.toolkit.workflow.content.UnorderedList;
import org.finos.symphony.toolkit.workflow.content.Word;
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
		List<Class<? extends Content>> allowedTypes = new ArrayList<Class<? extends Content>>();
		allowedTypes.add(BlockQuote.class);
		allowedTypes.add(Message.class);
		allowedTypes.add(OrderedList.class);
		allowedTypes.add(Paragraph.class);
		allowedTypes.add(Word.class);
		allowedTypes.add(UnorderedList.class);
		
		return new MessagePartWorkflowResolverFactory(allowedTypes);
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
	
	@Bean
	@ConditionalOnMissingBean
	public EntityMapResolverFactory entityMapResolverFactory() {
		return new EntityMapResolverFactory();
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
