package org.finos.springbot.teams.templating;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

import org.finos.springbot.workflow.templating.Rendering;
import org.finos.springbot.workflow.templating.TextFieldConverter;
import org.finos.springbot.workflow.templating.Variable;

import com.fasterxml.jackson.databind.JsonNode;

public class ValidatingTextFieldConverter extends TextFieldConverter<JsonNode> {

	public ValidatingTextFieldConverter(int priority, Rendering<JsonNode> r, Class<?>... forClass) {
		super(priority, r, forClass);
	}

	@Override
	public JsonNode apply(Field ctx, Type t, boolean editMode, Variable variable) {
		return super.apply(ctx, t, editMode, variable);
		
		// add validation
	}

	
}
