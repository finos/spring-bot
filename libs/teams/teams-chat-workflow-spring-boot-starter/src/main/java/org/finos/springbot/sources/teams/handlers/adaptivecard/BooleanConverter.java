package org.finos.springbot.sources.teams.handlers.adaptivecard;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

import org.finos.springbot.sources.teams.handlers.adaptivecard.helper.AdaptiveCardRendering;
import org.finos.springbot.workflow.templating.AbstractSimpleTypeConverter;
import org.finos.springbot.workflow.templating.Rendering;
import org.finos.springbot.workflow.templating.Variable;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class BooleanConverter extends AbstractSimpleTypeConverter<JsonNode> {

	public BooleanConverter(Rendering<JsonNode> r) {
		super(LOW_PRIORITY, r);
	}

	private boolean boolClass(Class<?> c) {
		return (Boolean.class.isAssignableFrom(c)) || (boolean.class.isAssignableFrom(c));
	}
	
	@Override
	public boolean canConvert(Field ctx, Type t) {
		return (t instanceof Class) && (boolClass((Class<?>) t));
	}

	@Override
	public JsonNode apply(Field ctx, Type t, boolean editMode, Variable variable) {
		if (editMode) {
			ObjectNode out = AdaptiveCardRendering.f.objectNode();
			out.put("type", "Input.Toggle");
			out.put("value", variable.getDataPath());
			out.put("id", variable.getFormFieldName());
			return out;
		} else {
			return r.text(variable); 
		}	
	}
	
}
