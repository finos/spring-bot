package org.finos.symphony.toolkit.workflow.sources.symphony.elements;

import java.lang.reflect.Type;
import java.util.Optional;

import org.finos.symphony.toolkit.workflow.Action;
import org.finos.symphony.toolkit.workflow.content.Addressable;
import org.finos.symphony.toolkit.workflow.java.resolvers.WorkflowResolver;
import org.finos.symphony.toolkit.workflow.java.resolvers.WorkflowResolverFactory;
import org.springframework.core.MethodParameter;

/**
 * Handles cases when the argument to the workflow is the contents of the form that the user just filled in.
 * 
 * @author moffrob
 *
 */
public class ElementsArgumentWorkflowResolverFactory implements WorkflowResolverFactory {

	@Override
	public WorkflowResolver createResolver(Action originatingAction) {
		if ((originatingAction instanceof ElementsAction) && (((ElementsAction) originatingAction).getFormData() != null)) {
			
			return new WorkflowResolver() {

				@Override
				public boolean canResolve(MethodParameter mp) {
					Type t = mp.getGenericParameterType();
					return ((Class<?>) t).isAssignableFrom(((ElementsAction) originatingAction).getFormData().getClass());
				}

				@Override
				public Optional<Object> resolve(MethodParameter mp) {
					if (canResolve(mp)) {
						return Optional.of(((ElementsAction) originatingAction).getFormData());
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
