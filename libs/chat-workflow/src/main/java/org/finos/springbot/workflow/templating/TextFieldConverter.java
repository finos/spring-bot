package org.finos.springbot.workflow.templating;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

public class TextFieldConverter<X> extends AbstractClassConverter<X> {

	public TextFieldConverter(int priority, Rendering<X> r, Class<?>... forClass) {
		super(priority, r, forClass);
	}

	@Override
	public X apply(Field ctx, Type t, boolean editMode, Variable variable) {
		return r.textField(variable, editMode);
	}


}
