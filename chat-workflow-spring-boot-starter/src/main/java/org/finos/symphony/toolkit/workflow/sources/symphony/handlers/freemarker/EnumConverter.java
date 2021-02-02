package org.finos.symphony.toolkit.workflow.sources.symphony.handlers.freemarker;

import java.lang.reflect.Type;
import java.util.Arrays;

import org.finos.symphony.toolkit.json.EntityJson;
import org.springframework.stereotype.Component;

@Component
public class EnumConverter extends AbstractSimpleTypeConverter {

	public EnumConverter() {
		super(LOW_PRIORITY);
	}

	@Override
	public boolean canConvert(Type t) {
		return (t instanceof Class) && ((Class<?>) t).isEnum();
	}

	@Override
	public String apply(Type t, boolean editMode, Variable variable, EntityJson ej) {
		if (editMode) {
			Class<?> c = (Class<?>) t;
			return renderDropdown(variable, 
					Arrays.asList(c.getEnumConstants()), variable.getFormFieldName(), 
					(g) -> ((Enum<?>)g).name(), 
					(g) -> g.toString(),
					(v, g) -> "((" + v.getDataPath()+"!'') == '"+g.toString()+"')?then('true', 'false')");
		} else {
			return text(variable, "!''");
		}
	}

}
