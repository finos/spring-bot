package org.finos.springbot.teams.templating;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

import org.finos.springbot.workflow.templating.AbstractClassConverter;
import org.finos.springbot.workflow.templating.Rendering;
import org.finos.springbot.workflow.templating.Variable;

import com.fasterxml.jackson.databind.JsonNode;

public class NumberConverter extends AbstractClassConverter<JsonNode> {

	public NumberConverter(Rendering<JsonNode> r) {
		super(LOW_PRIORITY, r, Number.class);
	}

	@Override
	public JsonNode apply(Field ctx, Type t, boolean editMode, Variable variable) {
		if (editMode) {
			return r.textField(variable, j -> j);
		} else {
			return r.text(variable);
		}
		
	}

}
