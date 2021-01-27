package org.finos.symphony.toolkit.workflow.sources.symphony.handlers.freemarker;

import java.lang.reflect.Field;

import org.finos.symphony.toolkit.json.EntityJson;
import org.springframework.stereotype.Component;

/**
 * This is the "fall-through" converter, used to convert user-beans when everything else fails.
 * 
 * @author rob@kite9.com
 *
 */
@Component
public class BeanConverter extends AbstractTableConverter {

	public BeanConverter() {
		super(BOTTOM_PRIORITY);
	}

	@Override
	public boolean canConvert(Field f) {
		return true;
	}

	@Override
	public String apply(Class<?> beanClass, Field f, boolean editMode, Variable variable, EntityJson ej,
			WithField context) {
		
		Class<?> c = f.getType();
		StringBuilder sb = new StringBuilder();
		
		//sb.append(createTable(beanClass, c, f, editMode, variable, ej, context, context, context))

		return sb.toString();
	}

}
