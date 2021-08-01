package org.finos.symphony.toolkit.workflow.sources.symphony.handlers.freemarker;

import java.lang.reflect.Field;
import java.util.List;

import org.finos.symphony.toolkit.workflow.sources.symphony.handlers.GetFields;

/**
 * Used where the content of the type contains fields of it's own.
 * 
 * @author rob@kite9.com
 *
 */
public abstract class AbstractComplexTypeConverter extends AbstractTypeConverter implements ComplexTypeConverter, GetFields {

	public AbstractComplexTypeConverter(int priority) {
		super(priority);
	}

	public String withFields(WithType controller, Class<?> c, boolean editMode, Variable variable, WithField displayer) {
		StringBuilder out = new StringBuilder();
		
		List<Field> fields = getFields(c);
		
		for (Field f : fields) {
			String text = displayer.apply(f, editMode, variable.field(f.getName()), controller);
			out.append(indent(variable.depth));
			out.append(text);
		}
	
		return out.toString();
	}
	
}
