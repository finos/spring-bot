package org.finos.springbot.workflow.templating;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

/**
 * General interface for performing some function against a type, with a given variable.
 */
public interface WithType<X> {

	/**
	 * @param ctx the field that this object is contained on, could be null.
	 * @param controller  Reference to the {@link AdaptiveCardConverter} calling this.
	 * @param t  Type being converted
	 * @param editMode Whether we are in edit mode, or not.
	 * @param variable  Used to place the type within the structure of the object it came from.
	 * @param details Used to decorate the output of any fields produced by this converter.
	 * @param ej  {@link EntityJson} that will be returned in the mesage.
	 * @return Part of a template.
	 */
	public X apply(Field ctx, WithType<X> controller, Type t, boolean editMode, Variable variable, WithField<X> details);

	public default TypeConverter<X> getConverter(Field ctx, Type t, WithType<X> ownerController) {
		return ownerController.getConverter(ctx, t, ownerController);
	}

}
