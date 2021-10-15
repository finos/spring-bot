package org.finos.springbot.sources.teams.handlers.adaptivecard;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

import org.finos.springbot.sources.teams.handlers.adaptivecard.helper.AdaptiveCardRendering;
import org.finos.springbot.workflow.templating.AbstractClassConverter;
import org.finos.springbot.workflow.templating.Rendering;
import org.finos.springbot.workflow.templating.Variable;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class NumberConverter extends AbstractClassConverter<JsonNode> {

	public NumberConverter(Rendering<JsonNode> r) {
		super(LOW_PRIORITY, r, Number.class);
	}

	@Override
	public JsonNode apply(Field ctx, Type t, boolean editMode, Variable variable) {
		if (editMode) {
			ObjectNode on = AdaptiveCardRendering.f.objectNode();
			on.put("type", "Input.Text");
			on.put("value", variable.getDataPath());
			on.put("id", variable.getFormFieldName());
			return on;
		} else {
			return r.text(variable);
		}
		
	}

}
