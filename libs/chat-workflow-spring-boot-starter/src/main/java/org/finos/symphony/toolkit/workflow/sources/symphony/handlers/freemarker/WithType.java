package org.finos.symphony.toolkit.workflow.sources.symphony.handlers.freemarker;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * General interface for performing some function against a type, with a given variable.
 */
public interface WithType {

	/**
	 * @param controller  Reference to the {@link FreemarkerFormMessageMLConverter} calling this.
	 * @param t  Type being converted
	 * @param editMode Whether we are in edit mode, or not.
	 * @param variable  Used to place the type within the structure of the object it came from.
	 * @param details Used to decorate the output of any fields produced by this converter.
	 * @param a Complex UI annotation to be rendered
	 * @return Part of a FreeMarker template.
	 */
	public String apply(WithType controller, Type t, boolean editMode, Variable variable, WithField details, Annotation a);

	
	public default TypeConverter getConverter(Type t, WithType ownerController) {
		return ownerController.getConverter(t, ownerController);
	}

}
