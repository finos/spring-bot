package org.finos.springbot.teams.templating;

import java.util.function.Function;

import org.finos.springbot.workflow.templating.Rendering;
import org.finos.springbot.workflow.templating.TextFieldConverter;

import com.fasterxml.jackson.databind.JsonNode;

public class StringConverter extends TextFieldConverter<JsonNode> {

	public StringConverter(int priority, Rendering<JsonNode> r, Class<?>... forClass) {
		super(priority, r, forClass);
	}

	protected Function<JsonNode, JsonNode> textFieldDetails() {
		return (j) -> j;
	}

}
