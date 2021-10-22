package org.finos.springbot.teams.templating;

import java.util.List;

import org.finos.springbot.workflow.annotations.WorkMode;
import org.finos.springbot.workflow.form.ButtonList;
import org.finos.springbot.workflow.templating.AbstractTopLevelConverter;
import org.finos.springbot.workflow.templating.Mode;
import org.finos.springbot.workflow.templating.Rendering;
import org.finos.springbot.workflow.templating.TypeConverter;
import org.finos.springbot.workflow.templating.Variable;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Takes a bean and converts it into a form with either an editable or display
 * version of MessageML.
 * 
 * @author Rob Moffat
 *
 */
public class AdaptiveCardTemplater extends AbstractTopLevelConverter<JsonNode, WorkMode> {

	
	public AdaptiveCardTemplater(List<TypeConverter<JsonNode>> fieldConverters, Rendering<JsonNode> r) {
		super(fieldConverters, r);
	}

	public static final String JUST_BUTTONS_FORM = "just-buttons-form";
	
	@Override
	public JsonNode convert(Class<?> c, Mode m) {
		Variable v = new ACVariable("form");
		
		JsonNodeFactory fact = new JsonNodeFactory(true);
		ObjectNode top = fact.objectNode();
		top.put("$schema", "http://adaptivecards.io/schemas/adaptive-card.json");
		top.put("version", "1.3");
		top.put("type","AdaptiveCard");
		JsonNode contents = apply(null, this, c, m==Mode.FORM, v, topLevelFieldOutput());
		ArrayNode body = top.putArray("body");
		body.add(contents);
		
		if (m == Mode.DISPLAY_WITH_BUTTONS || m == Mode.FORM) {
			// add some buttons
			body.add(r.buttons(ButtonList.KEY+".contents"));
		}
		
		return top;
	}



	
}
