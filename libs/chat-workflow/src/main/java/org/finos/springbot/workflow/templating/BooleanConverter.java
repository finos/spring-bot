package org.finos.springbot.workflow.templating;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

public class BooleanConverter<X> extends AbstractClassConverter<X> {

	public BooleanConverter(Rendering<X> r) {
		this(LOW_PRIORITY, r, Boolean.class, boolean.class);
	}
		
	public BooleanConverter(int priority, Rendering<X> r, Class<?>... forClass) {
		super(priority, r, forClass);
	}

	@Override
	public X apply(Field ctx, Type t, boolean editMode, Variable variable) {
		return r.checkBox(variable, editMode);
	}
	
}
