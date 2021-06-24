package org.finos.symphony.toolkit.workflow.java.resolvers;

import java.lang.reflect.Type;
import java.util.Optional;

import org.finos.symphony.toolkit.workflow.history.History;
import org.finos.symphony.toolkit.workflow.java.ConfigurableWorkflow;
import org.finos.symphony.toolkit.workflow.java.mapping.ChatHandlerExecutor;
import org.springframework.core.MethodParameter;

public class MessageHistoryWorkflowResolverFactory implements WorkflowResolverFactory {
	
	History hist;
	
	public MessageHistoryWorkflowResolverFactory(History hist) {
		this.hist = hist;
	}

	@Override
	public int priority() {
		return WorkflowResolverFactory.LOW_PRIORITY;
	}

	@Override
	public WorkflowResolver createResolver(ChatHandlerExecutor che) {
		return new WorkflowResolver() {
			
			@Override
			public Optional<Object> resolve(MethodParameter mp) {
//				if (isTarget) {
//					 return (Optional<Object>) hist.getLastFromHistory(c, a);
//				} else {
					return Optional.empty();
//				}
			}
			
			@Override
			public boolean canResolve(MethodParameter mo) {
				Type t = mo.getGenericParameterType();
				if (che.action().getWorkflow() instanceof ConfigurableWorkflow) {
					return ((ConfigurableWorkflow)che.action().getWorkflow()).getDataTypes().stream()
							.filter(dt -> dt.isAssignableFrom((Class<?>) t))
							.findFirst()
							.isPresent();
				} else {
					return false;
				}
			}
		};
	}

}
