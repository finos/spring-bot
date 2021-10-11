package org.finos.springbot.sources.teams.handlers.freemarker;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

public class NumberConverter extends AbstractClassConverter {

	public NumberConverter() {
		super(LOW_PRIORITY, Number.class);
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
