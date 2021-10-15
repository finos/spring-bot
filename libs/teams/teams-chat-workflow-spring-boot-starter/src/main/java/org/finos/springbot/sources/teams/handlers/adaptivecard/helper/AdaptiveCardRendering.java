package org.finos.springbot.sources.teams.handlers.adaptivecard.helper;

import java.util.List;

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
		out.put("weight", "Bolder");
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
	public JsonNode propertyPanel(List<JsonNode> contents) {
		ObjectNode out = f.objectNode();
		out.put("type", "Container");
		ArrayNode an = out.putArray("items");
		contents.forEach(c -> an.add(c));
		return out;
	}

	@Override
	public JsonNode property(String field, JsonNode value) {
		ObjectNode on = (ObjectNode) value;
		on.put("label", field);
		return on;
	}

	@Override
	public JsonNode button(String name, String text) {
		// TODO Auto-generated method stub
		return null;
	}

	
	
}
