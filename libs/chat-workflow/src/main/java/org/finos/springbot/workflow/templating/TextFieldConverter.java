package org.finos.springbot.workflow.templating;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.function.Function;

public class TextFieldConverter<X> extends AbstractClassConverter<X> {

	public TextFieldConverter(int priority, Rendering<X> r, Class<?>... forClass) {
		super(priority, r, forClass);
	}

	@Override
	public X apply(Field ctx, Type t, boolean editMode, Variable variable) {
		if (editMode) {
			return r.textField(variable, decoration());
		} else {
			return r.text(variable);
		}
	}

	protected Function<X, X> decoration() {
		return (j) -> j;
	}

}
