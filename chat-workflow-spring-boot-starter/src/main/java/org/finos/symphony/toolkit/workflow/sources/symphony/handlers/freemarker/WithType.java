package org.finos.symphony.toolkit.workflow.sources.symphony.handlers.freemarker;

import java.lang.reflect.Type;

import org.finos.symphony.toolkit.json.EntityJson;

/**
 * General interface for performing some function against a type, with a given variable.
 */
public interface WithType {

	public String apply(WithType controller, Type t, boolean editMode, Variable variable, EntityJson ej, WithField details);


}
