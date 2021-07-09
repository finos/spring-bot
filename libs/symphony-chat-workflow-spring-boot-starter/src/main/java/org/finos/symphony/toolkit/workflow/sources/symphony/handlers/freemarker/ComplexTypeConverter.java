package org.finos.symphony.toolkit.workflow.sources.symphony.handlers.freemarker;

import java.lang.reflect.Field;
import java.util.List;

public interface ComplexTypeConverter extends TypeConverter {

	String withFields(WithType controller, Class<?> c, boolean editMode, Variable variable, WithField displayer);
	
	List<Field> getFields(Class<?> c);
}
