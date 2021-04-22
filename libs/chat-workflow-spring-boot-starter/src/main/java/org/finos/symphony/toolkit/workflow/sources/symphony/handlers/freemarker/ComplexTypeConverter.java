package org.finos.symphony.toolkit.workflow.sources.symphony.handlers.freemarker;

import java.lang.reflect.Field;
import java.util.List;

import org.finos.symphony.toolkit.json.EntityJson;

public interface ComplexTypeConverter extends TypeConverter {

	public String withFields(WithType controller, Class<?> c, boolean editMode, Variable variable, EntityJson ej, WithField displayer);
	
	List<Field> getFields(Class<?> c);
}
