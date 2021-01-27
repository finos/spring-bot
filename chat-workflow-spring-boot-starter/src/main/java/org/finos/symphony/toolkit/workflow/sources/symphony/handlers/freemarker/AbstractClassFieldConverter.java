package org.finos.symphony.toolkit.workflow.sources.symphony.handlers.freemarker;

import java.lang.reflect.Field;

public abstract class AbstractClassFieldConverter extends AbstractFieldConverter {

	private Class<?> forClass;

	public AbstractClassFieldConverter(int priority, Class<?> forClass) {
		super(priority);
		this.forClass = forClass;
	}

	@Override
	public boolean canConvert(Field f) {
		return forClass.isAssignableFrom(f.getType());
	}
	
	
}
