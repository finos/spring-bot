package org.finos.springbot.teams.templating.helper;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.finos.springbot.workflow.templating.Rendering;
import org.finos.springbot.workflow.templating.Variable;
import org.springframework.util.StringUtils;

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
		out.put("text", nullProof(v));
		return out;
	}

	protected String nullProof(Variable v) {
		return "${if("+v.getDataPath()+",string("+v.getDataPath()+"),'')}";
	}

	@Override
	public JsonNode textField(Variable variable, Function<JsonNode, JsonNode> change) {
		ObjectNode on = AdaptiveCardRendering.f.objectNode();
		on.put("type", "Input.Text");
		on.put("value", nullProof(variable));
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
		if (!StringUtils.hasText(field)) {
			return null;
		} else if (value.get("type").asText().equals("TextBlock")) {
			ObjectNode out = f.objectNode();
			out.put("type", "FactSet");
			ObjectNode fact = f.objectNode();
			out.putArray("facts").add(fact);
			fact.put("title", field);
			fact.put("value", value.get("text").asText());
			return out;
		} else if (value.get("type").asText().equals("Input.Toggle")) {
			ObjectNode on = (ObjectNode) value;
			on.put("title", field);
			return on;
		} else if (value.get("type").asText().startsWith("Input.")) {
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
	public JsonNode renderDropdown(Variable variable, String location, String key, String value) {
		ObjectNode out = f.objectNode();
		out.put("type", "Input.ChoiceSet");
		out.put("value", "${"+variable.getDataPath()+"}");
		out.put("id", variable.getFormFieldName());
		ArrayNode an = out.putArray("choices");
		
		ObjectNode choice = f.objectNode();
		choice.put("$data", "${"+location+"}");
		choice.put("title", "${"+value+"}");
		choice.put("value", "${"+key+"}");
		an.add(choice);
		
		return out;
	}

	@Override
	public JsonNode renderDropdownView(Variable variable, String location, String key, String value) {
		ObjectNode out = f.objectNode();
		out.put("type", "TextBlock");
		out.put("text", nullProof(variable));  // TODO: This is the value, rather than the looked-up version
		return out;
	}

	@Override
	public JsonNode renderDropdown(Variable variable, Map<String, String> options) {
		ObjectNode out = f.objectNode();
		out.put("type", "Input.ChoiceSet");
		out.put("value", "${"+variable.getDataPath()+"}");
		out.put("id", variable.getFormFieldName());
		ArrayNode an = out.putArray("choices");
		
		options.forEach((k, v) -> {
			ObjectNode choice = f.objectNode();
			choice.put("title", k);
			choice.put("value", prettyPrint(v));
			an.add(choice);
		});
		
		return out;
	}

	private String prettyPrint(String v) {
		String text = v.substring(0, 1).toUpperCase()+v.substring(1).toLowerCase();
		text = text.replace('_',' ');
		return text;
	}

	@Override
	public JsonNode renderDropdownView(Variable variable, Map<String, String> options) {
		ObjectNode out = f.objectNode();
		out.put("type", "TextBlock");
		out.put("text", nullProof(variable));  // TODO: This is the value, rather than the looked-up version
		return out;
	}

	
	
	
	
}
