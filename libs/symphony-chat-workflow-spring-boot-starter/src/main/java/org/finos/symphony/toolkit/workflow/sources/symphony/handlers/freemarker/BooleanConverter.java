package org.finos.symphony.toolkit.workflow.sources.symphony.handlers.freemarker;

import java.lang.reflect.Type;

public class BooleanConverter extends AbstractSimpleTypeConverter {

	public BooleanConverter() {
		super(LOW_PRIORITY);
	}

	private boolean boolClass(Class<?> c) {
		return (Boolean.class.isAssignableFrom(c)) || (boolean.class.isAssignableFrom(c));
	}
	
	@Override
	public boolean canConvert(Type t) {
		return (t instanceof Class) && (boolClass((Class<?>) t));
	}

	@Override
	public String apply(Type t, boolean editMode, Variable variable) {
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
