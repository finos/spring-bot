package org.finos.springbot.workflow.java.resolvers;

import java.util.Optional;

import org.finos.springbot.workflow.actions.Action;
import org.finos.springbot.workflow.actions.FormAction;
import org.finos.springbot.workflow.java.mapping.ChatHandlerExecutor;

/**
 * Handles cases when the argument to the workflow is the contents of the form that the user just filled in.
 * 
 * @author moffrob
 *
 */
public class FormDataArgumentWorkflowResolverFactory implements WorkflowResolverFactory {

	private final class FormDataWorkflowResolver extends AbstractClassWorkflowResolver {
		private final Action originatingAction;

		private FormDataWorkflowResolver(Action originatingAction) {
			this.originatingAction = originatingAction;
		}

		@Override
		public boolean canResolve(Class<?> t) {
			return t.isAssignableFrom(((FormAction) originatingAction).getFormData().getClass());
		}

		@Override
		public Optional<Object> resolve(Class<?> t) {
			return Optional.of(((FormAction) originatingAction).getFormData());
		}
	}

	@Override
	public WorkflowResolver createResolver(ChatHandlerExecutor che) {
		Action originatingAction = che.action();
		if ((originatingAction instanceof FormAction) && (((FormAction) originatingAction).getFormData() != null)) {
			return new FormDataWorkflowResolver(originatingAction);
		} else {
			return new NullWorkflowResolver();
		}
	}

}
