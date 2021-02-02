package org.finos.symphony.toolkit.workflow.sources.symphony.handlers.freemarker;

import java.lang.reflect.Type;

public interface TypeConverter extends WithType {

	public int getPriority();
	
	public boolean canConvert(Type t);
		
}
