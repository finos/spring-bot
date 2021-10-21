package org.finos.springbot.teams.templating;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

import org.finos.springbot.workflow.content.User;
import org.finos.springbot.workflow.templating.AbstractClassConverter;
import org.finos.springbot.workflow.templating.Rendering;
import org.finos.springbot.workflow.templating.TextFieldConverter;
import org.finos.springbot.workflow.templating.Variable;

import com.fasterxml.jackson.databind.JsonNode;

public class UserConverter extends TextFieldConverter<JsonNode> {

	public UserConverter(Rendering<JsonNode> r) {
		super(LOW_PRIORITY, r, User.class);
	}

}
