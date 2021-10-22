package org.finos.springbot.workflow.templating;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class EnumConverter<X> extends AbstractSimpleTypeConverter<X> {

	public EnumConverter(Rendering<X> r) {
		super(LOW_PRIORITY, r);
	}

	@Override
	public boolean canConvert(Field ctx, Type t) {
		return (t instanceof Class) && ((Class<?>) t).isEnum();
	}

	@SuppressWarnings("unchecked")
	@Override
	public X apply(Field ctx, Type t, boolean editMode, Variable variable) {
		Map<String, String> options = generateEnumOptions((Class<Enum<?>>) t);
		return r.renderDropdown(variable, options, editMode);
	}

	protected Map<String, String> generateEnumOptions(Class<Enum<?>> t) {
		return Arrays.stream(t.getEnumConstants())
				.collect(Collectors.toMap(e -> e.toString(), e -> e.toString()));
		
	}

}
