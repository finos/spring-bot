`package org.finos.springbot.sources.teams.handlers.adaptivecard;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

import org.springframework.util.StringUtils;

/**
 * This is the "fall-through" converter, used to convert user-beans when everything else fails.
 *
 * @author rob@kite9.com
 *
 */
public class BeanConverter extends AbstractComplexTypeConverter {

	public BeanConverter() {
		super(BOTTOM_PRIORITY);
	}

	@Override
	public boolean canConvert(Field ctx, Type t) {
		return t instanceof Class<?>;
	}

	@Override
	public String apply(Field ctx, WithType controller, Type t, boolean editMode, Variable variable, WithField showDetails) {
		if (showDetails == null) {
			return "";
		}
		if (showDetails.expand()) {
			Class<?> element = (Class<?>) t;
			StringBuilder sb = new StringBuilder();
			sb.append(indent(variable.depth)+ "<table>");
			sb.append(withFields(controller, element, editMode, variable, wrapInTableCells(showDetails)));
			sb.append(indent(variable.depth)+ "</table>");
			return sb.toString();
		} else {
			return "some object";
		}
	}

	protected WithField wrapInTableCells(WithField inner) {
		return new WithField() {

            @Override
            public String apply(Field f, boolean editMode, Variable variable, WithType controller) {
				String fieldNameOrientation = getFieldNameOrientation(f);
				return StringUtils.hasText(fieldNameOrientation) ? "<tr><td><b>" + fieldNameOrientation + ":</b></td><td>" + inner.apply(f, editMode, variable, controller) + "</td></tr>" : "";
			}

			@Override
			public boolean expand() {
				return true;
			}
		};
	};


}
