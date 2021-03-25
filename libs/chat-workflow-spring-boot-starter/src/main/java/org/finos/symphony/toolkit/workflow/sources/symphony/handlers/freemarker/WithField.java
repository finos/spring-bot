package org.finos.symphony.toolkit.workflow.sources.symphony.handlers.freemarker;

import java.lang.reflect.Field;

import org.finos.symphony.toolkit.json.EntityJson;

/**
 * General interface for performing some function against a field, with a given variable.
 */
public interface WithField {

	public String apply(Field f, boolean editMode, Variable variable, EntityJson ej, WithType contentHandler);
	
	/**
	 * Return true if we are going to expand the contents of this field.
	 */
	public boolean expand();

}
