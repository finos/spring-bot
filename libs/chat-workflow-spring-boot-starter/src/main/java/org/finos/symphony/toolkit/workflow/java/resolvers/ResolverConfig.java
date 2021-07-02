package org.finos.symphony.toolkit.workflow.java.resolvers;

import java.util.Optional;

import org.finos.symphony.toolkit.workflow.content.Room;
import org.finos.symphony.toolkit.workflow.content.User;
import org.finos.symphony.toolkit.workflow.history.History;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
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
	
	/**
	 * Allows parameters to include {@link Room} or {@link Author} .
	 */
	@Bean 
	public WorkflowResolverFactory userRoomOrAddressableResolver() {
		
		return che -> new WorkflowResolver() {

			@Override
			public boolean canResolve(MethodParameter mp) {
				Class<?> c = mp.getParameterType();
				return (c.isAssignableFrom(Room.class))  || (c.isAssignableFrom(User.class)) ;
			}

			@Override
			public Optional<Object> resolve(MethodParameter mp) {
				Class<?> cl = mp.getParameterType();
				
				// handle room arg
				if ((cl.isAssignableFrom(Room.class)) && (che.action().getAddressable() instanceof Room)) {
					return Optional.of((Room) che.action().getAddressable());
				}
				
				// handle user arg
				if (cl.isAssignableFrom(User.class)) {
					return Optional.of(che.action().getUser());
				}
				
				return Optional.empty();
			}
			
		};
	}
	
//	
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
