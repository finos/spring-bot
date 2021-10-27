package org.finos.springbot.symphony.templating;

import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.finos.springbot.workflow.templating.Rendering;
import org.finos.springbot.workflow.templating.Variable;
import org.springframework.util.StringUtils;

public class FreemarkerRenderingOld implements Rendering<String> {

	@Override
	public String text(Variable variable, String suffix) {
		return "${"+variable.getDataPath()+suffix+"}";
	}
	
	@Override
	public String textField(Variable variable, Function<String, String> change) {
		return textField(variable, variable.getFormFieldName(), variable.getDisplayName());
	}
	
	
	@Override
	public String textField(Variable variable) {
	}
	
	@Override
	public String textField(Variable variable, String formFieldName, String displayName) {
		return formatErrorsAndIndent(formFieldName, variable.depth)
				+ "<text-field "
				+ attribute(variable, "name", formFieldName)
				+ attribute(variable, "placeholder", displayName) +
				">" + text(variable, "!''") + "</text-field>";
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
	
	@Override
	public String indent(int n) {
		return "\n"+String.format("%"+n+"s", "");
	}
	
	@Override
	public String formatErrorsAndIndent(String formField, int indent) {
		if (!StringUtils.hasText(formField)) {
			return indent(indent);
		} else {
			return indent(indent) 
				+ "<span class=\"tempo-text-color--red\">${entity.errors.contents['"+formField+"']!''}</span>"
				+ indent(indent);
		}
	}

	@Override
	public String attributeParam(Variable v, String name, String value) {
		return indent(v.depth+1) + name + "=\"${" + value + "}\"";
	}
	
	@Override
	public String attribute(Variable v, String name, String value) {
		return indent(v.depth+1) + name + "=\"" + value + "\"";
	}

	@Override
	public String beginIterator(Variable variable, Variable reg) {
		return indent(variable.depth) + "<#list "+variable.getDataPath()+" as "+reg.getDataPath()+">";
	}
	
	@Override
	public String endIterator(Variable variable) {
		return indent(variable.depth) + "</#list>";
	}

	@Override
	public String description(String d) {
		return d;
	}

	@Override
	public String list(List<String> contents) {
		return "<table>" + contents.stream().reduce(String::concat) + "</table>";
	}

	@Override
	public String addFieldName(String field, String value) {
		return field == null ? "" : "<tr><td><b>" + field + ":</b></td><td>" + value + "</td></tr>";
	}

	@Override
	public String button(String name, String text) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String checkboxView(Variable variable) {
		return text(variable, "?string(\"Y\", \"N\")");
	}

	@Override
	public String checkboxEdit(Variable variable) {
		return formatErrorsAndIndent(variable.getFormFieldName(), variable.depth) + 
				"<checkbox " 
				+ attribute(variable, "name", variable.getFormFieldName())
				+ attributeParam(variable, "checked", variable.getDataPath()+"?string('true', 'false')") 
				+ attribute(variable, "value", "true") 
				+ ">" 
				+ variable.getDisplayName()
				+ "</checkbox>";
		
	}

	@Override
	public String text(Variable v) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String renderDropdown(Variable variable, String location, Function<String, String> sourceFunction,
			Function<String, String> keyFunction, BiFunction<String, String, String> valueFunction) {
		// TODO Auto-generated method stub
		return null;
	}

	
}
