package org.finos.springbot.teams.templating.adaptivecard;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.finos.springbot.workflow.templating.Rendering;
import org.finos.springbot.workflow.templating.TextFieldConverter;
import org.finos.springbot.workflow.templating.Variable;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class TimeConverter extends TextFieldConverter<JsonNode> {

	public TimeConverter(Rendering<JsonNode> r) {
		super(LOW_PRIORITY, r, 
			Instant.class, 
			LocalDateTime.class, 
			ZoneId.class, 
			ZonedDateTime.class,
			LocalDate.class,
			LocalTime.class);
	}

	@Override
	public JsonNode apply(Field ctx, Type t, boolean editMode, Variable v) {
		ObjectNode on = (ObjectNode) super.apply(ctx, t, editMode, v);
		
//		Class<?> cl = (Class<?>) t;
//		
//		String field = editMode ? "value" : "text";
//		
//		if (Instant.class.isAssignableFrom(cl) || ZonedDateTime.class.isAssignableFrom(cl)) {
//			 on.put(field, AdaptiveCardRendering.nullProofWithFunction(v, "formatEpoch"));
//		} 
	
		return on;
	}
	

	
	
	
}
