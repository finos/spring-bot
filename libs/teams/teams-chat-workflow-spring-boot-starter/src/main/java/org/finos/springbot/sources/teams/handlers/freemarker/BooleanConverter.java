package org.finos.springbot.sources.teams.handlers.freemarker;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

public class BooleanConverter extends AbstractSimpleTypeConverter {

	public BooleanConverter() {
		super(LOW_PRIORITY);
	}

	private boolean boolClass(Class<?> c) {
		return (Boolean.class.isAssignableFrom(c)) || (boolean.class.isAssignableFrom(c));
	}
	
	@Override
	public boolean canConvert(Field ctx, Type t) {
		return (t instanceof Class) && (boolClass((Class<?>) t));
	}

	@Override
	public String apply(Field ctx, Type t, boolean editMode, Variable variable) {
		if (editMode) {
			return formatErrorsAndIndent(variable.getFormFieldName(), variable.depth) + 
				"<checkbox " 
				+ attribute(variable, "name", variable.getFormFieldName())
				+ attributeParam(variable, "checked", variable.getDataPath()+"?string('true', 'false')") 
				+ attribute(variable, "value", "true") 
				+ ">" 
				+ variable.getDisplayName()
				+ "</checkbox>";
		} else {
			return text(variable, "?string(\"Y\", \"N\")");
		}	
	}

	
}
