package org.finos.symphony.toolkit.workflow.sources.symphony.handlers.freemarker;

import java.lang.reflect.Type;

public class StringConverter extends AbstractClassConverter {

	public StringConverter() {
		super(LOW_PRIORITY, String.class);
	}

	@Override
	public String apply(Type t, boolean editMode, Variable variable) {
		if (editMode) {
			return formatErrorsAndIndent(variable)
					+ "<text-field "
					+ attribute(variable, "name", variable.getFormFieldName())
					+ attribute(variable, "placeholder", variable.getDisplayName()) +
					">" + text(variable, "!''") + "</text-field>";
		} else {
			return text(variable, "!''");
		}
	}

}
