package org.finos.springbot.sources.teams.handlers.adaptivecard;

import java.lang.reflect.Field;
import java.util.List;

public interface ComplexTypeConverter extends TypeConverter {

	String withFields(WithType controller, Class<?> c, boolean editMode, Variable variable, WithField displayer);
	
	List<Field> getFields(Class<?> c);
}
