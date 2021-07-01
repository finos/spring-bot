package org.finos.symphony.toolkit.workflow.sources.symphony.handlers.freemarker;

import org.finos.symphony.toolkit.json.EntityJson;

import java.lang.reflect.Field;
import java.util.List;

public interface ComplexTypeConverter extends TypeConverter {

	String withFields(WithType controller, Class<?> c, boolean editMode, Variable variable, EntityJson ej, WithField displayer);
	
	List<Field> getFields(Class<?> c);
}
