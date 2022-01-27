package org.finos.springbot.teams.templating.adaptivecard;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

import org.finos.springbot.workflow.templating.Rendering;
import org.finos.springbot.workflow.templating.TextFieldConverter;
import org.finos.springbot.workflow.templating.Variable;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Adds client-side validation for text fields, as far as this is supported in adaptive cards.
 * 
 * @author rob@kite9.com
 *
 */
public class ValidatingTextFieldConverter extends TextFieldConverter<JsonNode> {

	public ValidatingTextFieldConverter(int priority, Rendering<JsonNode> r, Class<?>... forClass) {
		super(priority, r, forClass);
	}

	@Override
	public JsonNode apply(Field ctx, Type t, boolean editMode, Variable variable) {
		return super.apply(ctx, t, editMode, variable);
		
		// add validation
	}

	
}
