package org.finos.springbot.workflow.templating;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;

public class CollectionConverter<X> extends AbstractComplexTypeConverter<X> {
	
	public CollectionConverter(Rendering<X> r) {
		super(LOW_PRIORITY, r);
	}
		
	@Override
	public boolean canConvert(Field ctx, Type t) {
		if (t instanceof ParameterizedType) {
			Type rawType = ((ParameterizedType)t).getRawType();
			return (rawType instanceof Class<?>) && Collection.class.isAssignableFrom((Class<?>) rawType);
		} else {
			return false;
		}
	}

	@Override
	public X apply(Field ctx, WithType<X> controller, Type t, boolean editMode, Variable variable, WithField<X> showDetail) {
		if (null == showDetail) {
			return r.description("...");
		} else if (showDetail.expand()) {
			Class<?> elementClass = (Class<?>) ((ParameterizedType) t).getActualTypeArguments()[0];
			TypeConverter<X> elementTypeConverter = controller.getConverter(null, elementClass, controller);
			Variable child = variable.index();
			X propertyPanel = elementTypeConverter.apply(null, controller, elementClass, false, child, collectionValues());
			return r.collection(elementClass, variable, child, propertyPanel, editMode);
		} else {
			return r.textField(variable, false);
		}
	}
	
	
	protected WithField<X> collectionValues() {
        return new WithField<X>() {

            @Override
            public boolean expand() {
                return true;
            }

            @Override
            public X apply(Field f, boolean editMode, Variable variable, WithType<X> contentHandler) {
            	Type t = f.getGenericType();
            	X out = contentHandler.apply(null, contentHandler, t, editMode, variable, null);
            	return out;
            }
        };

    }
	
	
}
