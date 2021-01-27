package org.finos.symphony.toolkit.workflow.sources.symphony.handlers.freemarker;

import java.lang.reflect.Field;

public interface FieldConverter extends WithField {

	public int getPriority();
	
	public boolean canConvert(Field f);
		
}
