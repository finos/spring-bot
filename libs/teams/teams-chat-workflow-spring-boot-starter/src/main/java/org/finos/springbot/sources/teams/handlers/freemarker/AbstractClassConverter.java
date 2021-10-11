package org.finos.springbot.sources.teams.handlers.freemarker;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

public abstract class AbstractClassConverter extends AbstractSimpleTypeConverter {

	private Class<?>[] forClass;

	public AbstractClassConverter(int priority, Class<?>... forClass) {
		super(priority);
		this.forClass = forClass;
	}

	@Override
	public boolean canConvert(Field ctx, Type t) {
		for (Class<?> class1 : forClass) {
			if ((t instanceof Class) && (class1.isAssignableFrom((Class<?>) t))) {
				return true;
			}
		}
		
		return false;
	}
	
	
}
