package org.finos.springbot.workflow.templating;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

import org.finos.springbot.workflow.annotations.Dropdown;

public class DropdownAnnotationConverter<X> extends AbstractSimpleTypeConverter<X> {

	public DropdownAnnotationConverter(Rendering<X> r) {
		super(MED_PRIORITY, r);
	}

	@Override
	protected X apply(Field ctx, Type t, boolean editMode, Variable variable) {
		Dropdown dd = getDropdownAnnotation(ctx);
		return r.renderDropdown(variable, "", dd.data(), dd.key(), dd.name(), editMode);
	}


	@Override
	public boolean canConvert(Field ctx, Type t) {
		return (getDropdownAnnotation(ctx) != null);
	}

	protected Dropdown getDropdownAnnotation(Field f) {
		return f == null ? null : f.getAnnotation(Dropdown.class);
	}

	
}
