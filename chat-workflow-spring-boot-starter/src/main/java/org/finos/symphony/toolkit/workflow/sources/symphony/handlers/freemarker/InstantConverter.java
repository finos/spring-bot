package org.finos.symphony.toolkit.workflow.sources.symphony.handlers.freemarker;

import java.lang.reflect.Field;
import java.time.Instant;

import org.finos.symphony.toolkit.json.EntityJson;
import org.springframework.stereotype.Component;

@Component
public class InstantConverter extends AbstractClassFieldConverter {

	public InstantConverter() {
		super(LOW_PRIORITY, Instant.class);
	}

	@Override
	public String apply(Class<?> beanClass, Field f, boolean editMode, Variable variable, EntityJson ej, WithField context) {
		if (editMode) {
			return textField(variable);
		} else {
			return text(variable, "!''");
		}
		
	}

}
