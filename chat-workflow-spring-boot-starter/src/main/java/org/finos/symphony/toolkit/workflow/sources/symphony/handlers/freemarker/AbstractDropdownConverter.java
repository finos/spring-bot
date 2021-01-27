package org.finos.symphony.toolkit.workflow.sources.symphony.handlers.freemarker;

import java.util.Collection;
import java.util.function.BiFunction;
import java.util.function.Function;

public abstract class AbstractDropdownConverter extends AbstractFieldConverter {

	public AbstractDropdownConverter(int priority) {
		super(priority);
	}


	protected <V> String renderDropdown(Variable v, Collection<V> options, String name, Function<V, String> keyFunction, Function<V, String> displayFunction, BiFunction<Variable, V,String> selectedFunction) {
		StringBuilder out = new StringBuilder();
		out.append("<select "+ attribute(v, "name", name));
		out.append(attribute(v, "required", "false"));
		out.append(attribute(v, "data-placeholder", "Choose "+v.getDisplayName()));
		out.append(">");
				
		for (V o : options) {
			out.append(indent(v.depth)+ "<option ");
			out.append(attribute(v, "value", keyFunction.apply(o)));
			out.append(attributeParam(v, "selected", selectedFunction.apply(v, o)));
			out.append(">");
			out.append(displayFunction.apply(o));
			out.append("</option>");
		}
		
		out.append("</select>");
		return out.toString();
	}
}
