package org.finos.springbot.teams.templating.thymeleaf;

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

public class TimeConverter extends TextFieldConverter<String> {

	public TimeConverter(Rendering<String> r) {
		super(LOW_PRIORITY, r, 
			Instant.class, 
			LocalDateTime.class, 
			ZoneId.class, 
			ZonedDateTime.class,
			LocalDate.class,
			LocalTime.class);
	}

	@Override
	public String apply(Field ctx, Type t, boolean editMode, Variable v) {
		String on = super.apply(ctx, t, editMode, v);
		
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
