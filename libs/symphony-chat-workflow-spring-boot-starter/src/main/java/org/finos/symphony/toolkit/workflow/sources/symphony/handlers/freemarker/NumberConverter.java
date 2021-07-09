package org.finos.symphony.toolkit.workflow.sources.symphony.handlers.freemarker;

import java.lang.reflect.Type;

public class NumberConverter extends AbstractClassConverter {

	public NumberConverter() {
		super(LOW_PRIORITY, Number.class);
	}

	@Override
	public String apply(Type t, boolean editMode, Variable variable) {
		if (editMode) {
			return formatErrorsAndIndent(variable) + "<text-field " 
					+ attribute(variable, "name", variable.getFormFieldName())
					+ attribute(variable, "placeholder", variable.getDisplayName())
					/*
					 * attribute("required", required)+ attribute("masked", masked)+
					 * attribute("maxlength", maxLength)+ attribute("minlength", minLength)+
					 */
					+ ">" 
					+ text(variable, "!''") 
					+ "</text-field>";
		} else {
			return text(variable, "!''");
		}
		
	}

}
