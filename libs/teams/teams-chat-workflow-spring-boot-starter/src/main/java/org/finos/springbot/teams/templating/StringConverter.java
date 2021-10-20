package org.finos.springbot.sources.teams.handlers.adaptivecard;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

import org.finos.springbot.workflow.templating.AbstractClassConverter;
import org.finos.springbot.workflow.templating.Rendering;
import org.finos.springbot.workflow.templating.Variable;

import com.fasterxml.jackson.databind.JsonNode;

public class StringConverter extends AbstractClassConverter<JsonNode> {

	public StringConverter(Rendering<JsonNode> r) {
		super(LOW_PRIORITY, r, String.class);
	}

	@Override
	public JsonNode apply(Field ctx, Type t, boolean editMode, Variable variable) {
		if (editMode) {
			return r.textField(variable, (j) -> j);
		} else {
			return r.text(variable);
		}
	}

}
