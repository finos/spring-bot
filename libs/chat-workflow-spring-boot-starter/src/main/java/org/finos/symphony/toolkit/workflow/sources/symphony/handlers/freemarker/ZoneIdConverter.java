package org.finos.symphony.toolkit.workflow.sources.symphony.handlers.freemarker;

import java.lang.reflect.Type;
import java.time.ZoneId;

import org.finos.symphony.toolkit.json.EntityJson;

public class ZoneIdConverter extends AbstractClassConverter {

	public ZoneIdConverter() {
		super(LOW_PRIORITY, ZoneId.class);
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
