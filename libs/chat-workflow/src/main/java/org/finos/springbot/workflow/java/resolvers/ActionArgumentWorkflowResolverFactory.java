package org.finos.springbot.workflow.java.resolvers;

import java.util.Optional;

import org.finos.springbot.workflow.actions.Action;
import org.finos.springbot.workflow.java.mapping.ChatHandlerExecutor;

/**
 * Handles cases when the argument to the workflow is the contents of the form that the user just filled in.
 * 
 * @author moffrob
 *
 */
public class ActionArgumentWorkflowResolverFactory implements WorkflowResolverFactory {

	private final class ActionWorkflowResolver extends AbstractClassWorkflowResolver {
		private final Action originatingAction;

		private ActionWorkflowResolver(Action originatingAction) {
			this.originatingAction = originatingAction;
		}

		@Override
		public boolean canResolve(Class<?> t) {
			return t.isAssignableFrom(originatingAction.getClass());
		}

		@Override
		public Optional<Object> resolve(Class<?> t) {
			return Optional.of(originatingAction);
		}
	}

	@Override
	public WorkflowResolver createResolver(ChatHandlerExecutor che) {
		Action originatingAction = che.action();
		return new ActionWorkflowResolver(originatingAction);
	}

}
