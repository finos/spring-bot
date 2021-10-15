package org.finos.springbot.workflow.templating;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Used where the content of the type contains fields of it's own.
 * 
 * @author rob@kite9.com
 *
 */
public abstract class AbstractComplexTypeConverter<X> extends AbstractTypeConverter<X> implements ComplexTypeConverter<X> {

	public AbstractComplexTypeConverter(int priority, Rendering<X> r) {
		super(priority, r);
	}

	public List<X> withFields(WithType<X> controller, Class<?> c, boolean editMode, Variable variable, WithField<X> displayer) {
		List<Field> fields = getFields(c);
		
		List<X> out = fields.stream()
			.map(f -> displayer.apply(f, editMode, variable.field(f.getName()), controller))
			.collect(Collectors.toList());
			
		return out;
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
