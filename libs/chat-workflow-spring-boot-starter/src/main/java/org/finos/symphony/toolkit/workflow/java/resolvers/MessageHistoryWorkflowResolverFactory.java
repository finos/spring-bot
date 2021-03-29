package org.finos.symphony.toolkit.workflow.java.resolvers;

import java.util.Optional;

import org.finos.symphony.toolkit.workflow.Action;
import org.finos.symphony.toolkit.workflow.content.Addressable;
import org.finos.symphony.toolkit.workflow.history.History;
import org.finos.symphony.toolkit.workflow.java.ConfigurableWorkflow;

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
	public WorkflowResolver createResolver(Action originatingAction) {
		return new WorkflowResolver() {
			
			@SuppressWarnings("unchecked")
			@Override
			public Optional<Object> resolve(Class<?> c, Addressable a, boolean isTarget) {
				if (isTarget) {
					 return (Optional<Object>) hist.getLastFromHistory(c, a);
				} else {
					return Optional.empty();
				}
			}
			
			@Override
			public boolean canResolve(Class<?> c) {
				if (originatingAction.getWorkflow() instanceof ConfigurableWorkflow) {
					return ((ConfigurableWorkflow)originatingAction.getWorkflow()).getDataTypes().stream()
							.filter(dt -> dt.isAssignableFrom(c))
							.findFirst()
							.isPresent();
				} else {
					return false;
				}
			}
		};
	}

}
