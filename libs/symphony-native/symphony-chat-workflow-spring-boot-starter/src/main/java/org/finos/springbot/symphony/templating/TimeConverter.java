package org.finos.springbot.symphony.templating;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import org.finos.springbot.workflow.templating.AbstractClassConverter;
import org.finos.springbot.workflow.templating.Rendering;
import org.finos.springbot.workflow.templating.Variable;

public class TimeConverter extends AbstractClassConverter<String> {

	public TimeConverter(Rendering<String> r) {
		this(LOW_PRIORITY, r, Instant.class, LocalDateTime.class, ZoneId.class);
	}

	public TimeConverter(int priority, Rendering<String> r, Class<?>... forClass) {
		super(priority, r, forClass);
	}

	@Override
	public String apply(Field ctx, Type t, boolean editMode, Variable variable) {
		return r.textField(variable, editMode);	
	}

}
