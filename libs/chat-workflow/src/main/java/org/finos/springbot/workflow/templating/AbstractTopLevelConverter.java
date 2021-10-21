package org.finos.springbot.workflow.templating;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class AbstractTopLevelConverter<X, MODE> implements WithType<X>, WorkTemplater<X, MODE> {

	private final List<TypeConverter<X>> converters;
	
	public AbstractTopLevelConverter(List<TypeConverter<X>> fieldConverters) {
		this.converters = new ArrayList<>(fieldConverters);
		Collections.sort(this.converters, (a, b) -> Integer.compare(a.getPriority(), b.getPriority()));
	}


	@Override
	public TypeConverter<X> getConverter(Field ctx, Type t, WithType<X> ownerController) {
		for(TypeConverter<X> fc : converters) {
			if (fc.canConvert(ctx, t)) {
				return fc;
			}
		} 
		
		throw new UnsupportedOperationException("No converter found for "+t);
	}

	@Override
	public X apply(Field ctx, WithType<X> controller, Type t, boolean editMode, Variable variable, WithField<X> context) {
		TypeConverter<X> tc = getConverter(ctx, t, controller);
		return tc.apply(ctx, controller, t, editMode, variable, context);
	}

	/**
	 * This is the with-field apply.  It doesn't add any wrapper onto the output.
	 */
	public WithField<X> topLevelFieldOutput() {
		
		return new WithField<X>() {
			
			public X apply(Field f, boolean editMode, Variable variable, WithType<X> contentHandler) {
				Type t = f.getGenericType();
				return contentHandler.apply(f, AbstractTopLevelConverter.this, t, editMode, variable, topLevelFieldOutput());
			}

			@Override
			public boolean expand() {
				return true;
			}
		};
		
	}
	
	

}