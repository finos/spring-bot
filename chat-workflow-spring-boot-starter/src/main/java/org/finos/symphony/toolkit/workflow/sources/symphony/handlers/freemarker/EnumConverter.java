package org.finos.symphony.toolkit.workflow.sources.symphony.handlers.freemarker;

import java.lang.reflect.Field;
import java.util.Arrays;

import org.finos.symphony.toolkit.json.EntityJson;
import org.springframework.stereotype.Component;

@Component
public class EnumConverter extends AbstractDropdownConverter {

	public EnumConverter() {
		super(LOW_PRIORITY);
	}

	@Override
	public boolean canConvert(Field f) {
		return f.getType().isEnum();
	}

	@Override
	public String apply(Class<?> beanClass, Field f, boolean editMode, Variable variable, EntityJson ej,
			WithField context) {
		if (editMode) {
			Class<?> c = f.getType();
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
