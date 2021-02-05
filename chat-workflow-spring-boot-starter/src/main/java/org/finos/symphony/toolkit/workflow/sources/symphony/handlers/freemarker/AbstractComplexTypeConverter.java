package org.finos.symphony.toolkit.workflow.sources.symphony.handlers.freemarker;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.finos.symphony.toolkit.json.EntityJson;

/**
 * Used where the content of the type contains fields of it's own.
 * 
 * @author rob@kite9.com
 *
 */
public abstract class AbstractComplexTypeConverter extends AbstractTypeConverter {

	public AbstractComplexTypeConverter(int priority) {
		super(priority);
	}

	public static String withFields(WithType controller, Class<?> c, boolean editMode, Variable variable, EntityJson ej, WithField displayer) {
		StringBuilder out = new StringBuilder();
		if ((c != Object.class) && (c!=null)) {
			out.append(withFields(controller, c.getSuperclass(), editMode, variable, ej, displayer));
	
			for (Field f : c.getDeclaredFields()) {
				if (!Modifier.isStatic(f.getModifiers())) {
					String text = displayer.apply(f, editMode, variable.field(f.getName()), ej, controller);
					out.append(indent(variable.depth));
					out.append(text);
				}
			}
		}
	
		return out.toString();
	}

}
