package org.finos.springbot.sources.teams.handlers.adaptivecard;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * Used where the content of the type contains fields of it's own.
 * 
 * @author rob@kite9.com
 *
 */
public abstract class AbstractComplexTypeConverter extends AbstractTypeConverter implements ComplexTypeConverter {

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

	@Override
	public List<Field> getFields(Class<?> c) {
		List<Field> out = new ArrayList<Field>();
		
		if ((c != Object.class) && (c!=null)) {
			
			out.addAll(getFields(c.getSuperclass()));
			
			for (Field f : c.getDeclaredFields()) {
				if (!Modifier.isStatic(f.getModifiers())) {
					out.add(f);
				}
			}
		} 
		
		return out;
	}

	
}
