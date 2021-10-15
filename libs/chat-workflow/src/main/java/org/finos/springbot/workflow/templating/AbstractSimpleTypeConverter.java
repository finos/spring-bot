package org.finos.springbot.workflow.templating;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

/**
 * Outputs simple (i.e. single-field) type information.
 * 
 * @author rob@kite9.com
 *
 */
public abstract class AbstractSimpleTypeConverter<X> extends AbstractTypeConverter<X> implements SimpleTypeConverter<X> {

	public AbstractSimpleTypeConverter(int priority, Rendering<X> r) {
		super(priority, r);
	}

	@Override
	public final X apply(Field ctx, WithType<X> controller, Type t, boolean editMode, Variable variable, WithField<X> notUsed) {
		return apply(ctx, t, editMode, variable);
	}

	protected abstract X apply(Field ctx, Type t, boolean editMode, Variable variable);

}
