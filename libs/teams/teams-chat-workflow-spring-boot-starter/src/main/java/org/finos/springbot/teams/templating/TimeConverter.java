package org.finos.springbot.teams.templating;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.finos.springbot.workflow.templating.Rendering;
import org.finos.springbot.workflow.templating.TextFieldConverter;

import com.fasterxml.jackson.databind.JsonNode;

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
	

}
