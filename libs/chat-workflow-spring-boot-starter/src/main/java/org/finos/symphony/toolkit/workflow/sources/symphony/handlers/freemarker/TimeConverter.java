package org.finos.symphony.toolkit.workflow.sources.symphony.handlers.freemarker;

import java.lang.reflect.Type;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import org.finos.symphony.toolkit.json.EntityJson;

public class TimeConverter extends AbstractClassConverter {

	public TimeConverter() {
		super(LOW_PRIORITY, Instant.class, LocalDateTime.class, ZoneId.class);
	}

	@Override
	public String apply(Type t, boolean editMode, Variable variable, EntityJson ej) {
		if (editMode) {
			return textField(variable);
		} else {
			return text(variable, "!''");
		}
		
	}

}
