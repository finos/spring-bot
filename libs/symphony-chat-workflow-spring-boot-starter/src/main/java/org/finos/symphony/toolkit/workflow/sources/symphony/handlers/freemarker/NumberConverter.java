package org.finos.symphony.toolkit.workflow.sources.symphony.handlers.freemarker;

import java.lang.reflect.Type;

public class NumberConverter extends AbstractClassConverter {

	public NumberConverter() {
		super(LOW_PRIORITY, Number.class);
	}

	@Override
	public String apply(Type t, boolean editMode, Variable variable) {
		if (editMode) {
			return textField(variable);
		} else {
			return text(variable, "!''");
		}
		
	}

}
