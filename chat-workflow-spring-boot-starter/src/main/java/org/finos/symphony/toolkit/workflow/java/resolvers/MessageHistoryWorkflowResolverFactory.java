package org.finos.symphony.toolkit.workflow.java.resolvers;

import java.util.Optional;

import org.finos.symphony.toolkit.workflow.Action;
import org.finos.symphony.toolkit.workflow.content.Addressable;
import org.finos.symphony.toolkit.workflow.java.ConfigurableWorkflow;
import org.finos.symphony.toolkit.workflow.sources.symphony.history.MessageHistory;

public class MessageHistoryWorkflowResolverFactory implements WorkflowResolverFactory {
	
	MessageHistory hist;
	
	public MessageHistoryWorkflowResolverFactory(MessageHistory hist) {
		this.hist = hist;
	}
	

	@Override
	public WorkflowResolver createResolver(Action originatingAction) {
		return new WorkflowResolver() {
			
			@SuppressWarnings("unchecked")
			@Override
			public Optional<Object> resolve(Class<?> c, Addressable a, boolean isTarget) {
				return (Optional<Object>) hist.getLastFromHistory(c, a);
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
