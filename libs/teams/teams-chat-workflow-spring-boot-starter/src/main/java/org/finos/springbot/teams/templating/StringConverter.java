package org.finos.springbot.teams.templating;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.function.Function;

import org.finos.springbot.workflow.templating.AbstractClassConverter;
import org.finos.springbot.workflow.templating.Rendering;
import org.finos.springbot.workflow.templating.Variable;

public class StringConverter<X> extends AbstractClassConverter<X> {

	public StringConverter(int priority, Rendering<X> r, Class<?>... forClass) {
		super(priority, r, forClass);
	}

	@Override
	public X apply(Field ctx, Type t, boolean editMode, Variable variable) {
		if (editMode) {
			return r.textField(variable, textFieldDetails());
		} else {
			return r.text(variable);
		}
	}

	protected Function<X, X> textFieldDetails() {
		return (j) -> j;
	}

}
