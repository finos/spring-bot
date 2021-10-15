package org.finos.springbot.workflow.templating;

import java.lang.reflect.Field;
import java.util.List;

public interface ComplexTypeConverter<X> extends TypeConverter<X> {

	List<X> withFields(WithType<X> controller, Class<?> c, boolean editMode, Variable variable, WithField<X> displayer);
	
	List<Field> getFields(Class<?> c);
}
