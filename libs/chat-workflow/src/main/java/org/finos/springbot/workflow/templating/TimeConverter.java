package org.finos.springbot.workflow.templating;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * Really, really basic time converter that just uses a text field.
 * 
 * @author rob@kite9.com
 */
public class TimeConverter<X> extends AbstractClassConverter<X> {

	public TimeConverter(Rendering<X> r) {
		this(LOW_PRIORITY, r, 	
				Instant.class, 
				LocalDateTime.class, 
				ZoneId.class, 
				ZonedDateTime.class,
				LocalDate.class,
				LocalTime.class);
	}

	public TimeConverter(int priority, Rendering<X> r, Class<?>... forClass) {
		super(priority, r, forClass);
	}

	@Override
	public X apply(Field ctx, Type t, boolean editMode, Variable variable) {
		return r.textField(variable, editMode);	
	}

}
