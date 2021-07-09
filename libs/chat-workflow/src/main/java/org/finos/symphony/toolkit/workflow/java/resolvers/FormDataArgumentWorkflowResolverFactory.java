package org.finos.symphony.toolkit.workflow.java.resolvers;

import java.lang.reflect.Type;
import java.util.Optional;

import org.finos.symphony.toolkit.workflow.actions.Action;
import org.finos.symphony.toolkit.workflow.actions.FormAction;
import org.finos.symphony.toolkit.workflow.java.mapping.ChatHandlerExecutor;
import org.springframework.core.MethodParameter;

/**
 * Handles cases when the argument to the workflow is the contents of the form that the user just filled in.
 * 
 * @author moffrob
 *
 */
public class FormDataArgumentWorkflowResolverFactory implements WorkflowResolverFactory {

	@Override
	public WorkflowResolver createResolver(ChatHandlerExecutor che) {
		Action originatingAction = che.action();
		if ((originatingAction instanceof FormAction) && (((FormAction) originatingAction).getFormData() != null)) {
			
			return new WorkflowResolver() {

				@Override
				public boolean canResolve(MethodParameter mp) {
					Type t = mp.getGenericParameterType();
					return ((Class<?>) t).isAssignableFrom(((FormAction) originatingAction).getFormData().getClass());
				}

				@Override
				public Optional<Object> resolve(MethodParameter mp) {
					if (canResolve(mp)) {
						return Optional.of(((FormAction) originatingAction).getFormData());
					} else {
						return Optional.empty();
					}
				}
			};
		} else {
			return new WorkflowResolver() {

				@Override
				public boolean canResolve(MethodParameter c) {
					return false;
				}

				@Override
				public Optional<Object> resolve(MethodParameter mp) {
					return Optional.empty();
				}
			};
		}
	}

}
