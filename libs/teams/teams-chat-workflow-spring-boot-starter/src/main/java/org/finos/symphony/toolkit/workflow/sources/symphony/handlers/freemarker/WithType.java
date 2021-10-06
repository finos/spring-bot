package org.finos.symphony.toolkit.workflow.sources.symphony.handlers.freemarker;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

import org.finos.symphony.toolkit.json.EntityJson;

/**
 * General interface for performing some function against a type, with a given variable.
 */
public interface WithType {

	/**
	 * @param ctx the field that this object is contained on, could be null.
	 * @param controller  Reference to the {@link FreemarkerFormMessageMLConverter} calling this.
	 * @param t  Type being converted
	 * @param editMode Whether we are in edit mode, or not.
	 * @param variable  Used to place the type within the structure of the object it came from.
	 * @param details Used to decorate the output of any fields produced by this converter.
	 * @param ej  {@link EntityJson} that will be returned in the mesage.
	 * @return Part of a FreeMarker template.
	 */
	public String apply(Field ctx, WithType controller, Type t, boolean editMode, Variable variable, WithField details);

	
	public default TypeConverter getConverter(Field ctx, Type t, WithType ownerController) {
		return ownerController.getConverter(ctx, t, ownerController);
	}

}
