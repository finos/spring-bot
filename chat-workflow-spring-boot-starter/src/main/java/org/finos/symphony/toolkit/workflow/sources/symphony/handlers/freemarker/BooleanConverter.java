package org.finos.symphony.toolkit.workflow.sources.symphony.handlers.freemarker;

import java.lang.reflect.Field;

import org.finos.symphony.toolkit.json.EntityJson;

public class BooleanConverter extends AbstractFieldConverter {

	public BooleanConverter() {
		super(LOW_PRIORITY);
	}

	private boolean boolClass(Class<?> c) {
		return (Boolean.class.isAssignableFrom(c)) || (boolean.class.isAssignableFrom(c));
	}
	
	@Override
	public boolean canConvert(Field f) {
		return boolClass(f.getType());
	}

	@Override
	public String apply(Class<?> beanClass, Field f, boolean editMode, Variable variable, EntityJson ej,
			WithField context) {
		if (editMode) {
			return formatErrorsAndIndent(variable) + 
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
