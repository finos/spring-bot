package org.finos.symphony.toolkit.workflow.sources.symphony.elements;

import java.util.Optional;

import org.finos.symphony.toolkit.workflow.Action;
import org.finos.symphony.toolkit.workflow.content.Addressable;
import org.finos.symphony.toolkit.workflow.java.resolvers.WorkflowResolver;
import org.finos.symphony.toolkit.workflow.java.resolvers.WorkflowResolverFactory;

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
				public boolean canResolve(Class<?> cl) {
					return cl.isAssignableFrom(((ElementsAction) originatingAction).getFormData().getClass());
				}

				@Override
				public Optional<Object> resolve(Class<?> cl, Addressable a, boolean isTarget) {
					if (cl.isAssignableFrom(((ElementsAction) originatingAction).getFormData().getClass())) {
						return Optional.of(((ElementsAction) originatingAction).getFormData());
					} else {
						return Optional.empty();
					}
				}
			};
		} else {
			return new WorkflowResolver() {

				@Override
				public boolean canResolve(Class<?> c) {
					return false;
				}

				@Override
				public Optional<Object> resolve(Class<?> c, Addressable a, boolean isTarget) {
					return Optional.empty();
				}
			};
		}
	}

}
