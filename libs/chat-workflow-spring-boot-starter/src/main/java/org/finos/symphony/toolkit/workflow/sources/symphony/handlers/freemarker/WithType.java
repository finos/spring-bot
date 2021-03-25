package org.finos.symphony.toolkit.workflow.sources.symphony.handlers.freemarker;

import java.lang.reflect.Type;

import org.finos.symphony.toolkit.json.EntityJson;

/**
 * General interface for performing some function against a type, with a given variable.
 */
public interface WithType {

	/**
	 * @param controller  Reference to the {@link FreemarkerFormMessageMLConverter} calling this.
	 * @param t  Type being converted
	 * @param editMode Whether we are in edit mode, or not.
	 * @param variable  Used to place the type within the structure of the object it came from.
	 * @param ej  {@link EntityJson} that will be returned in the mesage.
	 * @param details Used to decorate the output of any fields produced by this converter.
	 * @return Part of a FreeMarker template.
	 */
	public String apply(WithType controller, Type t, boolean editMode, Variable variable, EntityJson ej, WithField details);


}
