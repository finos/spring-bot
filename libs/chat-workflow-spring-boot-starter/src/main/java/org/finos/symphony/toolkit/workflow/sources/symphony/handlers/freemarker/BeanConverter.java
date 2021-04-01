package org.finos.symphony.toolkit.workflow.sources.symphony.handlers.freemarker;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Optional;

import org.finos.symphony.toolkit.json.EntityJson;
import org.finos.symphony.toolkit.workflow.sources.symphony.handlers.freemarker.annotations.DisableAttribute;
import org.finos.symphony.toolkit.workflow.sources.symphony.handlers.freemarker.annotations.DisplayAttribute;

import static java.util.Optional.ofNullable;

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
	public boolean canConvert(Type t) {
		return t instanceof Class<?>;
	}

	@Override
	public String apply(WithType controller, Type t, boolean editMode, Variable variable, EntityJson ej, WithField showDetails) {
		if (showDetails.expand()) {
			Class<?> element = (Class<?>) t;
			StringBuilder sb = new StringBuilder();
			sb.append(indent(variable.depth)+ "<table>");
			sb.append(withFields(controller, element, editMode, variable, ej, wrapInTableCells(showDetails)));
			sb.append(indent(variable.depth)+ "</table>");
			return sb.toString();
		} else {
			return "some object";
		}
	}

	protected WithField wrapInTableCells(WithField inner) {
		return new WithField() {

			@Override
			public String apply(Field f, boolean editMode, Variable variable, EntityJson ej, WithType controller) {

				Boolean isAttributeEnabled = ofNullable(f.getAnnotation(DisableAttribute.class)).map(DisableAttribute::isEnabled).orElse(false);

				return !isAttributeEnabled ? ofNullable(f.getAnnotation(DisplayAttribute.class)).map(
						displayAttribute -> "<tr><td><b>" + ofNullable(displayAttribute.name()).orElse(f.getName()) + ":</b></td><td>" + inner.apply(f, editMode, variable, ej, controller) + "</td></tr>"
				).orElse("<tr><td><b>" + f.getName() + ":</b></td><td>" + inner.apply(f, editMode, variable, ej, controller) + "</td></tr>") : "";

			}

			@Override
			public boolean expand() {
				return true;
			}
		};
	};


}
