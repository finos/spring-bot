package org.finos.springbot.sources.teams.handlers.adaptivecard;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class TimeConverter extends AbstractClassConverter {

	public TimeConverter() {
		this(LOW_PRIORITY, Instant.class, LocalDateTime.class, ZoneId.class);
	}

	public TimeConverter(int priority, Class<?>... forClass) {
		super(priority, forClass);
	}

	@Override
	public String apply(Field ctx, Type t, boolean editMode, Variable variable) {
		if (editMode) {
			return textField(variable);
		} else {
			return text(variable, "!''");
		}
		
	}

}
