package org.finos.springbot.sources.teams.handlers.adaptivecard;

import java.util.List;

import org.finos.springbot.sources.teams.handlers.Mode;
import org.finos.springbot.sources.teams.handlers.WorkConverter;
import org.finos.springbot.workflow.templating.AbstractTopLevelConverter;
import org.finos.springbot.workflow.templating.TypeConverter;
import org.finos.springbot.workflow.templating.Variable;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Takes a bean and converts it into a form with either an editable or display
 * version of MessageML.
 * 
 * @author Rob Moffat
 *
 */
public class AdaptiveCardConverter extends AbstractTopLevelConverter<JsonNode> implements WorkConverter<Mode, JsonNode> {

	
	public AdaptiveCardConverter(List<TypeConverter<JsonNode>> fieldConverters) {
		super(fieldConverters);
	}

	public static final String JUST_BUTTONS_FORM = "just-buttons-form";
	
	@Override
	public JsonNode convert(Class<?> c, Mode m) { //, Object o, ButtonList actions, boolean editMode, Errors e, EntityJson work) {
		Variable v = new ACVariable("$root.form");
		
		JsonNodeFactory fact = new JsonNodeFactory(true);
		ObjectNode top = fact.objectNode();
		top.put("$schema", "http://adaptivecards.io/schemas/adaptive-card.json");
		top.put("version", "1.3");
		top.put("type","AdaptiveCard");
		JsonNode contents = apply(null, this, c, m==Mode.FORM, v, topLevelFieldOutput());
		top.putArray("body").add(contents);
		
//		
//		if (m == Mode.FORM) {
//			sb.append("\n<form " + AbstractTypeConverter.attribute(v, "id", c.getCanonicalName()) + ">");
//		} 
//		
//		JsonNode contents =
//		
//		if (m == Mode.DISPLAY_WITH_BUTTONS) {
//			// the form is created here just to contain these buttons.
//			sb.append("\n<form " + AbstractTypeConverter.attribute(v, "id", JUST_BUTTONS_FORM) + ">");
//			sb.append(handleButtons());
//			sb.append("\n</form>");
//		} else if (m == Mode.FORM) { 
//			sb.append(handleButtons());
//			sb.append("\n</form>");
//		} 
//
//		sb.append("\n<#-- ending template -->\n");
		return top;
	}

//	private List<JsonNode> handleButtons() {
//		StringBuilder sb = new StringBuilder();
//		sb.append("\n  <p><#list entity.buttons.contents as button>");
//		sb.append("\n    <button ");
//		sb.append("\n         name=\"${button.name}\"");
//		sb.append("\n         type=\"${button.buttonType?lower_case}\">");
//		sb.append("\n      ${button.text}");
//		sb.append("\n    </button>");
//		sb.append("\n  </#list></p>");
//		return sb.toString();
//	}
//	
//	

	
}
