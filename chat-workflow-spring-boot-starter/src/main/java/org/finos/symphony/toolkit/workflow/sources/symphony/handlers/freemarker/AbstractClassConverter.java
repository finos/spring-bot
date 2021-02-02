package org.finos.symphony.toolkit.workflow.sources.symphony.handlers.freemarker;

import java.lang.reflect.Type;

public abstract class AbstractClassConverter extends AbstractSimpleTypeConverter {

	private Class<?> forClass;

	public AbstractClassConverter(int priority, Class<?> forClass) {
		super(priority);
		this.forClass = forClass;
	}

	@Override
	public boolean canConvert(Type t) {
		return (t instanceof Class) && forClass.isAssignableFrom((Class<?>) t);
	}
	
	
}
