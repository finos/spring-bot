package org.finos.symphony.toolkit.workflow.sources.symphony.handlers.freemarker;

import java.util.function.Function;

import org.finos.symphony.toolkit.workflow.sources.symphony.handlers.freemarker.annotations.ComplexUIType;

/**
 * 
 * @author Suresh Rupnar
 *
 */
public abstract class AbstractComplexUIConverter extends AbstractComplexTypeConverter {

	public AbstractComplexUIConverter(int priority) {
		super(priority);
	}

	public String createComplexUIElement(Variable variable, String mappedBy, String mappedClass,
			ComplexUIType complexUIType) {
		return renderDropdown(variable, "entity." + mappedClass + "." + mappedBy, variable.getFormFieldName(),
				(v) -> "((" + v.getDataPath() + "!'') == '${v}')?then('true', 'false')");
	}

	public <V> String renderDropdown(Variable v, String location, String name,
			Function<Variable, String> selectedFunction) {
		StringBuilder out = new StringBuilder();
		out.append("<select " + AbstractTypeConverter.attribute(v, "name", name));
		out.append(AbstractTypeConverter.attribute(v, "required", "false"));
		out.append(AbstractTypeConverter.attribute(v, "data-placeholder", "Choose " + v.getDisplayName()));
		out.append(">");

		out.append(AbstractTypeConverter.indent(v.getDepth()) + "<#list " + location + " as v>");
		out.append(AbstractTypeConverter.indent(v.getDepth()) + "<option ");
		out.append(AbstractTypeConverter.attribute(v, "value", "${v}"));
		out.append(AbstractTypeConverter.attributeParam(v, "selected", selectedFunction.apply(v)));
		out.append(">");
		out.append("${v}");
		out.append("</option>");
		out.append(AbstractTypeConverter.indent(v.getDepth()) + "</#list>");

		out.append("</select>");
		return out.toString();
	}
}