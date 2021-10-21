package org.finos.springbot.teams.templating;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;

import org.finos.springbot.workflow.templating.AbstractComplexTypeConverter;
import org.finos.springbot.workflow.templating.Rendering;
import org.finos.springbot.workflow.templating.TypeConverter;
import org.finos.springbot.workflow.templating.Variable;
import org.finos.springbot.workflow.templating.WithField;
import org.finos.springbot.workflow.templating.WithType;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class CollectionConverter extends AbstractComplexTypeConverter<JsonNode> {
	
	public CollectionConverter(Rendering<JsonNode> r) {
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
	public JsonNode apply(Field ctx, WithType<JsonNode> controller, Type t, boolean editMode, Variable variable, WithField<JsonNode> showDetail) {
		if (null == showDetail) {
			return r.description("...");
		} else if (showDetail.expand()) {
			Class<?> elementClass = (Class<?>) ((ParameterizedType) t).getActualTypeArguments()[0];
			TypeConverter<JsonNode> elementTypeConverter = controller.getConverter(null, elementClass, controller);
			Variable child = variable.index();
			JsonNode propertyPanel = elementTypeConverter.apply(null, controller, elementClass, false, child, collectionValues());
			((ObjectNode) propertyPanel).put("$data", "${"+variable.getDataPath()+"}");
			return propertyPanel;
		} else {
			return r.text(variable);
		}
	}
	
	
	protected WithField<JsonNode> collectionValues() {
        return new WithField<JsonNode>() {

            @Override
            public boolean expand() {
                return true;
            }

            @Override
            public JsonNode apply(Field f, boolean editMode, Variable variable, WithType<JsonNode> contentHandler) {
            	Type t = f.getGenericType();
            	JsonNode out = contentHandler.apply(null, contentHandler, t, editMode, variable, null);
            	
            	// add checkbox/delete button
            	
            	return out;
            }
        };

    }
	
	
}
