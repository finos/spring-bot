package org.finos.springbot.teams.templating;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.finos.springbot.workflow.actions.form.TableDeleteRows;
import org.finos.springbot.workflow.actions.form.TableEditRow;
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
		//out.put("weight", "bolder");
		out.put("wrap", true);
		return out;
	}
	
	protected String nullProof(Variable v) {
		return nullProofWithFunction(v, "");
	}
	
	protected String nullProofWithFunction(Variable v, String function) {
		return "${if("+v.getDataPath()+","+function+"("+v.getDataPath()+"),'')}";
	}
	
	protected String nullProofWithExtension(Variable v, String ext) {
		return "${if("+v.getDataPath()+","+v.getDataPath()+extend(ext)+",'')}";
	}
	
	protected String fromOption(Variable v, String ext, String options, String optionsExt, String optionsVal) {
		return "${if("+v.getDataPath()+extend(ext)+",first(where("+options+", o, o"+extend(optionsExt)+" == "+v.getDataPath()+"))"+extend(optionsVal)+",'')}";
	}

	@Override
	public JsonNode textField(Variable variable, boolean editMode) {
		if (editMode) {
			ObjectNode on = AdaptiveCardRendering.f.objectNode();
			on.put("type", "Input.Text");
			on.put("value", nullProof(variable));
			on.put("id", variable.getFormFieldName());
			return on;
		} else {
			ObjectNode out = f.objectNode();
			out.put("type", "TextBlock");
			out.put("text", nullProof(variable));
			return out;
		}
	}
		
	@Override
	public ObjectNode list(Class<?> on, List<JsonNode> contents, boolean editMode) {
		ObjectNode out = rows("emphasis", contents);
		return out;
	}
	
	private ObjectNode rows(String style, List<JsonNode> rows) {
		return rows(style, rows.toArray(JsonNode[]::new));
	}
	
	private ObjectNode rows(String style, JsonNode... rows) {
		ObjectNode out = f.objectNode();
		out.put("type", "Container");
		if (StringUtils.hasText(style)) {
			out.put("style", style);
		}
		ArrayNode an = out.putArray("items");
		Arrays.stream(rows).forEach(c -> an.add(c));
		return out;
	}
	
	private ObjectNode columns(JsonNode... items) {
		ObjectNode out = f.objectNode();
		out.put("type", "ColumnSet");
		out.put("separator", true);
		ArrayNode content = out.putArray("columns");
		Arrays.stream(items).forEach(i -> {
			ObjectNode col = f.objectNode();
			col.put("type", "Column");
			col.put("verticalContentAlignment", "Center");
			ArrayNode colContent = col.putArray("items");
			colContent.add(i);
			content.add(col);
		});
		return out;
	}

	@Override
	public JsonNode addFieldName(String field, JsonNode value) {
		if (!StringUtils.hasText(field)) {
			return null;
		} else if (value.get("type").asText().equals("Input.Toggle")) {
			ObjectNode on = (ObjectNode) value;
			on.put("title", field);
			return on;
		} else if (value.get("type").asText().startsWith("Input.")) {
			ObjectNode on = (ObjectNode) value;
			on.put("label", field);
			return on;
		} else {
			JsonNode desc = description(field);
			ObjectNode out = rows("", desc, rows("default", value));
			return out;
		}
	}
	
	public JsonNode buttons(String location) {
		ObjectNode out = f.objectNode();
		out.put("type", "ActionSet");
		ArrayNode actions = out.putArray("actions");
		ObjectNode submit = f.objectNode();
		submit.put("type", "Action.Submit");
		submit.put("title", "${text}");
		submit.put("id", "${name}");
		submit.put("associatedInputs","auto");
		ObjectNode map = submit.putObject("data");
		map.put("action", "${name}");
		map.put("form", "${$root.formid}");
		submit.put("$data", "${"+location+"}");
		actions.add(submit);
		return out;
	}

	@Override
	public JsonNode button(String name, String value) {
		ObjectNode out = f.objectNode();
		out.put("type", "ActionSet");
		ArrayNode actions = out.putArray("actions");
		ObjectNode submit = f.objectNode();
		submit.put("type", "Action.Submit");
		submit.put("title", name);
		submit.put("id", value);
		ObjectNode map = submit.putObject("data");
		map.put("action", name);
		map.put("form", "${root.formid}");
		actions.add(submit);
		return out;
	}
	
	

	@Override
	public JsonNode renderDropdown(Variable variable, String variableKey, String location, String key, String value, boolean editMode) {
		if (editMode) {
			ObjectNode out = f.objectNode();
			out.put("type", "Input.ChoiceSet");
			out.put("value", "${"+variable.getDataPath()+extend(variableKey)+"}");
			out.put("id", variable.getFormFieldName());
			ArrayNode an = out.putArray("choices");
			
			ObjectNode choice = f.objectNode();
			choice.put("$data", "${"+location+"}");
			choice.put("title", "${"+value+"}");
			choice.put("value", "${"+key+"}");
			an.add(choice);
			
			return out;
		} else {
			ObjectNode out = f.objectNode();
			out.put("type", "TextBlock");
			out.put("text", fromOption(variable, variableKey, location, key, value)); 
			return out;
		}
	}

	@Override
	public JsonNode renderDropdown(Variable variable, String variableKey, Map<String, String> options, boolean editMode) {
		if (editMode) {
			ObjectNode out = f.objectNode();
			out.put("type", "Input.ChoiceSet");
			out.put("value", "${"+variable.getDataPath()+extend(variableKey)+"}");
			out.put("id", variable.getFormFieldName());
			ArrayNode an = out.putArray("choices");
			
			options.forEach((k, v) -> {
				ObjectNode choice = f.objectNode();
				choice.put("title", k);
				choice.put("value", prettyPrint(v));
				an.add(choice);
			});
			
			return out;
		} else {
			ObjectNode out = f.objectNode();
			out.put("type", "TextBlock");
			out.put("text", nullProofWithExtension(variable, variableKey));  // TODO: This is the value, rather than the looked-up version
			return out;
		}
	}

	private String prettyPrint(String v) {
		String text = v.substring(0, 1).toUpperCase()+v.substring(1).toLowerCase();
		text = text.replace('_',' ');
		return text;
	}

	@Override
	public JsonNode checkBox(Variable variable, boolean editMode) {
		if (editMode) {
			ObjectNode out = AdaptiveCardRendering.f.objectNode();
			out.put("type", "Input.Toggle");
			out.put("value", "${"+ variable.getDataPath()+"}");
			out.put("id", variable.getFormFieldName());
			return out;
		} else {
			ObjectNode out = AdaptiveCardRendering.f.objectNode();
			out.put("type", "TextBlock");
			out.put("text", "${if("+variable.getDataPath()+",'☑','☐')}");
			return out;
		}
	}

	@Override
	public JsonNode collection(Type t, Variable v, JsonNode in, boolean editable) {
		if (editable) {
			JsonNode cb = addFieldName("Select", checkBox(v, true));
			JsonNode edit = button("Edit", v.getDataPath()+TableEditRow.EDIT_SUFFIX);
			ObjectNode footer = columns(cb, edit);
			ObjectNode out = rows("default", in, footer);
			out.put("$data", "${"+v.getDataPath()+"}");
			
			JsonNode delete = button("Delete Selected", v.getDataPath()+TableDeleteRows.ACTION_SUFFIX);
			
			return rows("", out, delete);
		} else {
			((ObjectNode)in).put("$data", "${"+v.getDataPath()+"}");
			return in;
		}
	}

	@Override
	public JsonNode table(Variable v, JsonNode headers, JsonNode body) {
		throw new UnsupportedOperationException("Adaptive Cards doesn't support tables yet");
	}

	@Override
	public JsonNode userDisplay(Variable v) {
		ObjectNode out = f.objectNode();
		out.put("type", "TextBlock");
		out.put("text", nullProofWithExtension(v, "name"));
		return out;
	}
	
}
