package org.finos.springbot.sources.teams.handlers.adaptivecard;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

import org.finos.springbot.workflow.annotations.Dropdown;
import org.finos.springbot.workflow.annotations.Dropdown.ElementFormat;
import org.finos.springbot.workflow.templating.AbstractSimpleTypeConverter;
import org.finos.springbot.workflow.templating.Rendering;
import org.finos.springbot.workflow.templating.Variable;

import com.fasterxml.jackson.databind.JsonNode;

public class DropdownAnnotationConverter extends AbstractSimpleTypeConverter<JsonNode> {

	public DropdownAnnotationConverter(Rendering<JsonNode> r) {
		super(MED_PRIORITY, r);
	}

	@Override
	protected JsonNode apply(Field ctx, Type t, boolean editMode, Variable variable) {
		Dropdown dd = getDropdownAnnotation(ctx);
		String location = dd.data();
		Class<? extends ElementFormat> format = dd.format();
		ElementFormat instance = instantiateDropdownFormat(format);
		if (editMode) {
			return renderDropdown(variable, location, instance);
		} else {
			return "${" + instance.getValueFunction().apply(variable.getDataPath(), location) +  "!''}";
		}
	}

	protected ElementFormat instantiateDropdownFormat(Class<? extends ElementFormat> format) {
		ElementFormat instance;
		try {
			instance = format.getConstructor().newInstance();
		} catch (Exception e) {
			throw new RuntimeException("Couldn't instantiate "+format+".  Make sure it is static and has a no-args constructor", e);
		}
		return instance;
	}

	@Override
	public boolean canConvert(Field ctx, Type t) {
		return (getDropdownAnnotation(ctx) != null) && super.canConvert(null, t);
	}

	protected Dropdown getDropdownAnnotation(Field f) {
		return f == null ? null : f.getAnnotation(Dropdown.class);
	}

	
}
