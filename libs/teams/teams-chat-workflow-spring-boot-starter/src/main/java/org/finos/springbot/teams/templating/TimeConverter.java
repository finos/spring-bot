package org.finos.springbot.sources.teams.handlers.adaptivecard;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import org.finos.springbot.workflow.templating.AbstractClassConverter;
import org.finos.springbot.workflow.templating.Rendering;
import org.finos.springbot.workflow.templating.Variable;

import com.fasterxml.jackson.databind.JsonNode;

public class TimeConverter extends AbstractClassConverter<JsonNode> {

	public TimeConverter(Rendering<JsonNode> r) {
		this(LOW_PRIORITY, r, Instant.class, LocalDateTime.class, ZoneId.class);
	}

	public TimeConverter(int priority, Rendering<JsonNode> r, Class<?>... forClass) {
		super(priority, r, forClass);
	}

	@Override
	public JsonNode apply(Field ctx, Type t, boolean editMode, Variable variable) {
		if (editMode) {
			return r.textField(variable, j -> j);
		} else {
			return r.text(variable);
		}
		
	}

}
