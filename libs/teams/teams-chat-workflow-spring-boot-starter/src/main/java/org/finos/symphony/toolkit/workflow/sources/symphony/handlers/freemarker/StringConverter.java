package org.finos.symphony.toolkit.workflow.sources.symphony.handlers.freemarker;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

public class StringConverter extends AbstractClassConverter {

	public StringConverter() {
		super(LOW_PRIORITY, String.class);
	}

	@Override
	public String apply(Field ctx, Type t, boolean editMode, Variable variable) {
		if (editMode) {
			return textField(variable);
		} else {
			return text(variable, "!''");
		}
	}

}
