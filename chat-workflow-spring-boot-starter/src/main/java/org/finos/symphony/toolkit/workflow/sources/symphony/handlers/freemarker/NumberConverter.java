package org.finos.symphony.toolkit.workflow.sources.symphony.handlers.freemarker;

import java.lang.reflect.Field;

import org.finos.symphony.toolkit.json.EntityJson;
import org.springframework.stereotype.Component;

@Component
public class NumberConverter extends AbstractClassFieldConverter {

	public NumberConverter() {
		super(LOW_PRIORITY, Number.class);
	}

	@Override
	public String apply(Class<?> beanClass, Field f, boolean editMode, Variable variable, EntityJson ej, WithField context) {
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
