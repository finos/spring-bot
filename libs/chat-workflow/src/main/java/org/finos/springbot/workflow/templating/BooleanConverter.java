package org.finos.springbot.workflow.templating;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

import com.fasterxml.jackson.databind.JsonNode;

public class BooleanConverter extends AbstractClassConverter<JsonNode> {

	public BooleanConverter(Rendering<JsonNode> r) {
		this(LOW_PRIORITY, r, Boolean.class, boolean.class);
	}
		
	public BooleanConverter(int priority, Rendering<JsonNode> r, Class<?>... forClass) {
		super(priority, r, forClass);
	}

	@Override
	public JsonNode apply(Field ctx, Type t, boolean editMode, Variable variable) {
		return r.checkBox(variable, editMode);
	}
	
}
