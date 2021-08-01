package org.finos.symphony.toolkit.workflow.sources.symphony.handlers.freemarker;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * Outputs simple (i.e. single-field) type information.
 * 
 * @author rob@kite9.com
 *
 */
public abstract class AbstractSimpleTypeConverter extends AbstractTypeConverter implements SimpleTypeConverter {

	public AbstractSimpleTypeConverter(int priority) {
		super(priority);
	}

	@Override
	public final String apply(WithType controller, Type t, boolean editMode, Variable variable, WithField notUsed, Annotation a) {
		return apply(t, editMode, variable);
	}

	protected abstract String apply(Type t, boolean editMode, Variable variable);

}
