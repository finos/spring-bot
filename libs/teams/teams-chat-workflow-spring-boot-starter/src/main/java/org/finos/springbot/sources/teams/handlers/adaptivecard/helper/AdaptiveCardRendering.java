package org.finos.springbot.sources.teams.handlers.adaptivecard.helper;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.finos.springbot.workflow.templating.Rendering;
import org.finos.springbot.workflow.templating.Variable;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class AdaptiveCardRendering implements Rendering<JsonNode> {
	
	public static JsonNodeFactory f = new JsonNodeFactory(true);


	@Override
	public JsonNode description(String d) {
		ObjectNode out = f.objectNode();
		out.put("type", "TextBlock");
		out.put("text", d);
		//out.put("weight", "Bolder");
		out.put("wrap", true);
		return out;
	}
	
	@Override
	public JsonNode text(Variable v) {
		ObjectNode out = f.objectNode();
		out.put("type", "TextBlock");
		out.put("text", v.getDataPath());
		return out;
	}

	@Override
	public JsonNode textField(Variable variable, Function<JsonNode, JsonNode> change) {
		ObjectNode on = AdaptiveCardRendering.f.objectNode();
		on.put("type", "Input.Text");
		on.put("value", variable.getDataPath());
		on.put("id", variable.getFormFieldName());
		return change.apply(on);
	}
	

	
	@Override
	public ObjectNode propertyPanel(List<JsonNode> contents) {
		ObjectNode out = f.objectNode();
		out.put("type", "Container");
		out.put("style", "emphasis");
		ArrayNode an = out.putArray("items");
		contents.forEach(c -> an.add(c));
		return out;
	}

	@Override
	public JsonNode property(String field, JsonNode value) {
		if (value.get("type").asText().startsWith("Input.")) {
			ObjectNode on = (ObjectNode) value;
			on.put("label", field);
			return on;
		} else {
			ObjectNode container = propertyPanel(
				Arrays.asList(description(field), propertyPanel(Arrays.asList(value))));
			
			container.put("style", "default");
			return container;
//			
//			out.put("type", "Container");
//			ObjectNode nameColumn = f.objectNode();
//			nameColumn.put("type", "TextBlock");
//			nameColumn.put("width", "auto");
//			nameColumn.putArray("items").add(description(field));
//			
//			ObjectNode valueColumn = f.objectNode();
//			valueColumn.put("type", "Column");
//			valueColumn.put("width", "stretch");
//			valueColumn.putArray("items").add(value);
//			
//			ArrayNode columns = out.putArray("columns");
//			columns.add(nameColumn);
//			columns.add(valueColumn);
//			return out;
		}
	}

	@Override
	public JsonNode button(String name, String text) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JsonNode renderDropdown(Variable variable, String location, Function<String, String> sourceFunction,
			Function<String, String> keyFunction, BiFunction<String, String, String> valueFunction) {
		
		"type": "Input.ChoiceSet",
        "choices": [
            {
                "title": "Choice 1",
                "value": "Choice 1"
            },
            {
                "title": "Choice 2",
                "value": "Choice 2"
            },
            {
                "title": "three",
                "value": "three"
            }
        ],
        "placeholder": "Placeholder text"
    }

	
	
	
}
