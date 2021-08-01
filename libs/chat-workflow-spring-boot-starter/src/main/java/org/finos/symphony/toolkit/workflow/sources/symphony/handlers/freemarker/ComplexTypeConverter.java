package org.finos.symphony.toolkit.workflow.sources.symphony.handlers.freemarker;

public interface ComplexTypeConverter extends TypeConverter {

	String withFields(WithType controller, Class<?> c, boolean editMode, Variable variable, WithField displayer);
}
