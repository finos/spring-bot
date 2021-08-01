package org.finos.symphony.toolkit.workflow.sources.symphony.handlers;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.finos.symphony.toolkit.workflow.sources.symphony.handlers.freemarker.annotations.ComplexUI;

/**
 * @author rupnsur
 *
 */
public interface GetFields {

	public default List<Field> getFields(Class<?> c) {
		List<Field> out = new ArrayList<Field>();

		if ((c != Object.class) && (c != null)) {

			out.addAll(getFields(c.getSuperclass()));

			for (Field f : c.getDeclaredFields()) {
				if (!Modifier.isStatic(f.getModifiers())) {
					out.add(f);
				}
			}
		}

		return out;
	}

	public default boolean isComplextUIField(Field f) {
		return f.getAnnotation(ComplexUI.class) != null ? true : false;
	}
}