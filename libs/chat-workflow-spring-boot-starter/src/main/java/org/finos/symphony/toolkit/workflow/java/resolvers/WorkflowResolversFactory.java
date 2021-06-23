package org.finos.symphony.toolkit.workflow.java.resolvers;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.finos.symphony.toolkit.workflow.java.mapping.ChatHandlerExecutor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.MethodParameter;

/**
 * Returns the {@link WorkflowResolvers}, which chains together lots of {@link WorkflowResolver} calls into a single one.
 * 
 * @author moffrob
 *
 */
public class WorkflowResolversFactory implements ApplicationContextAware {
	
	/**
	 * Using the application context here allows us to avoid a dependency loop, where we don't have all the {@link WorkflowResolverFactory}'s
	 * ready at the same time.
	 */
	private ApplicationContext beanFactory;

	public WorkflowResolversFactory() {
		super();
	}
	

	public WorkflowResolvers createResolvers(ChatHandlerExecutor che) {
		final List<WorkflowResolver> resolvers = beanFactory.getBeansOfType(WorkflowResolverFactory.class)
				.values().stream()
				.sorted((a,b) -> Integer.compare(a.priority(), b.priority()))
				.map(wrf -> wrf.createResolver(che))
				.collect(Collectors.toList());
		
		return new WorkflowResolvers() {

			@Override
			public Optional<Object> resolve(MethodParameter mp) {
				for (WorkflowResolver workflowResolver : resolvers) {
					Optional<Object> oo = workflowResolver.resolve(mp);
					if (oo.isPresent()) {
						return oo;
					}
				}
				
				return Optional.empty();
			}

			@Override
			public boolean canResolve(MethodParameter t) {
				for (WorkflowResolver workflowResolver : resolvers) {
					boolean b = workflowResolver.canResolve(t);
					if (b) {
						return true;
					}
				}
				
				return false;
			}

		};
	}


	@Override
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.beanFactory = applicationContext;
	}
}
