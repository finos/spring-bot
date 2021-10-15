package org.finos.springbot.workflow.templating;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.List;

/**
 * This is the "fall-through" converter, used to convert user-beans when everything else fails.
 *
 * @author rob@kite9.com
 *
 */
public class BeanConverter<X> extends AbstractComplexTypeConverter<X> {

	public BeanConverter(Rendering<X> r) {
		super(BOTTOM_PRIORITY, r);
	}

	@Override
	public boolean canConvert(Field ctx, Type t) {
		return t instanceof Class<?>;
	}

	@Override
	public X apply(Field ctx, WithType<X> controller, Type t, boolean editMode, Variable variable, WithField<X> showDetails) {
		if (showDetails == null) {
			return null;
		}
		
		if (showDetails.expand()) {
			Class<?> element = (Class<?>) t;
			List<X> contents = withFields(controller, element, editMode, variable, propertyPanel(showDetails));
			return r.propertyPanel(contents);
		} else {
			return r.description("some object");
		}
	}

	protected WithField<X> propertyPanel(WithField<X> inner) {
		return new WithField<X>() {

            @Override
            public X apply(Field f, boolean editMode, Variable variable, WithType<X> controller) {
				String fieldNameOrientation = getFieldNameOrientation(f);
				return r.property(fieldNameOrientation, inner.apply(f, editMode, variable, controller));
			}

			@Override
			public boolean expand() {
				return true;
			}
		};
	};


}
