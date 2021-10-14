package org.finos.springbot.sources.teams.handlers.adaptivecard;

import java.lang.reflect.Field;
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
	public final String apply(Field ctx, WithType controller, Type t, boolean editMode, Variable variable, WithField notUsed) {
		return apply(ctx, t, editMode, variable);
	}

	protected abstract String apply(Field ctx, Type t, boolean editMode, Variable variable);

}
