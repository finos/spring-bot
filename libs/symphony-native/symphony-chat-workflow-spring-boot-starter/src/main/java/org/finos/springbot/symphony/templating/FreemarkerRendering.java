package org.finos.springbot.symphony.templating;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.finos.springbot.workflow.templating.Rendering;
import org.finos.springbot.workflow.templating.Variable;
import org.springframework.util.StringUtils;

public class FreemarkerRendering implements Rendering<String> {

	@Override
	public String description(String d) {
		return "<span>"+d+"</span>";
	}

	@Override
	public String list(Class<?> of, List<String> contents, boolean editable) {
		return "<table>" + contents.stream().reduce((a, b) -> a + "\n" + b).orElse("") + "</table>";
	}

	@Override
	public String addFieldName(String field, String value) {
		return !StringUtils.hasText(field) ? "" : "<tr><td style=\"width: 200px\"><b>" + field + ":</b></td><td>" + value + "</td></tr>";
	}

	@Override
	public String renderDropdown(Variable variable, String variableKey, String choiceLocation, String choiceKey, String choiceValue, boolean editable) {
		String index = variable.index().getDataPath();
		int indent = ((FreemarkerVariable) variable).depth;

		if (editable) {
			return formatErrorsAndIndent(variable.getFormFieldName(), indent)
					+ "<div style=\"min-height: 100px;\"><select " 
					+ attribute(variable, "name", variable.getFormFieldName())
					+ attribute(variable, "data-placeholder", "Choose "+variable.getDisplayName())
					+ ">"
					+ indent(indent)
					+ "  <#list entity."+choiceLocation+" as "+index+">"
					+ indent(indent)
					+ "   <option value=\"${"+index+choiceKey+"}\""
					+ " selected=\"${((("+variable.getDataPath()+variableKey+")!'') == "+index+choiceKey+")?string('true','false')}\""
					+ ">"
					+ indent(indent)
					+ "    ${"+index+choiceValue+"}"
					+ indent(indent)
					+ "   </option>"
					+ indent(indent)
					+" </#list>"
					+ indent(indent)
					+"</select></div>";
			
		} else {
			return indent(indent)
					+ "  <#list entity."+choiceLocation+" as "+index+">"
					+ indent(indent)
					+ "   <#if ("+index+choiceKey+" == ("+variable.getDataPath()+variableKey+")!'')>"
					+ indent(indent)
					+ "    ${"+index+choiceValue+"}"
					+ indent(indent)
					+ "   </#if>"
					+ indent(indent)
					+ "  </#list>";

		}
	}

	private String mapToOptions(Map<String, String> options, Variable v, String variableKey) {
		return options.entrySet().stream()
			.map(e -> convertToOption(e, v, variableKey))
			.reduce((a, b) -> a + "\n"+ b)
			.orElse("");

	}
	
	private String convertToOption(Entry<String, String> e, Variable v, String variableKey) {
		return "   <option value=\""+e.getKey()+"\" selected=\"${((("+v.getDataPath()+variableKey+")!'') == '"+e.getKey()+"')?string('true','false')}\">"  	
				+ e.getValue()+"</option>";
	}
	
	private String mapToIfs(Map<String, String> options, Variable v, String variableKey) {
		return options.entrySet().stream()
			.map(e -> convertToIf(e, v, variableKey))
			.reduce((a, b) -> a + "\n"+ b)
			.orElse("");

	}
	
	private String convertToIf(Entry<String, String> e, Variable v, String variableKey) {
		return "   <#if (("+v.getDataPath()+variableKey+")!'') == '"+e.getKey()+"'>"  	
				+ e.getValue()+"</#if>";
	}

	@Override
	public String renderDropdown(Variable variable, String variableKey, Map<String, String> options, boolean editable) {
		int indent = ((FreemarkerVariable) variable).depth;

		if (editable) {
			return formatErrorsAndIndent(variable.getFormFieldName(), indent)
					+ "<div style=\"min-height: 100px;\"><select " 
					+ attribute(variable, "name", variable.getFormFieldName())
					+ attribute(variable, "data-placeholder", "Choose "+variable.getDisplayName())
					+ ">"
					+ indent(indent)
					+ mapToOptions(options, variable, variableKey)
					+ indent(indent)
					+"</select></div>";
			
		} else {
			return indent(indent) + mapToIfs(options, variable, variableKey);
		}
	}

	@Override
	public String textField(Variable variable, boolean editable) {
		String formFieldName = variable.getFormFieldName();
		if (editable) {
			return formatErrorsAndIndent(formFieldName, ((FreemarkerVariable) variable).depth)
					+ "<text-field "
					+ attribute(variable, "name", formFieldName)
					+ attribute(variable, "placeholder", "") +
					">" + text(variable,"",  "!''") + "</text-field>";
		} else {
			return text(variable, "", "!''");
		}
	}

	@Override
	public String checkBox(Variable variable, boolean editable) {
		if (!editable) {
			return text(variable, "", "?string(\"Y\", \"N\")");
		} else {
			return formatErrorsAndIndent(variable.getFormFieldName(), ((FreemarkerVariable) variable).depth) + 
					"<checkbox " 
					+ attribute(variable, "name", variable.getFormFieldName())
					+ attributeParam(variable, "checked", variable.getDataPath()+"?string('true', 'false')") 
					+ attribute(variable, "value", "true") 
					+ ">" 
					+ variable.getDisplayName()
					+ "</checkbox>";
			
		}
	}

	@Override
	public String collection(Type t, Variable v, String in, boolean editable) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String button(String name, String value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String buttons(String location) {
		// TODO Auto-generated method stub
		return null;
	}
	
	private String attributeParam(Variable v, String name, String value) {
		return indent(((FreemarkerVariable) v).depth+1) + name + "=\"${" + value + "}\"";
	}
	
	private String attribute(Variable v, String name, String value) {
		return indent(((FreemarkerVariable)v).depth+1) + name + "=\"" + value + "\"";
	}

	private String text(Variable variable, String extension, String suffix) {
		return "${("+variable.getDataPath()+extension+")"+suffix+"}";
	}

	private String formatErrorsAndIndent(String formField, int indent) {
		if (!StringUtils.hasText(formField)) {
			return indent(indent);
		} else {
			return indent(indent) 
				+ "<span class=\"tempo-text-color--red\">${entity.errors.contents['"+formField+"']!''}</span>"
				+ indent(indent);
		}
	}
	
	private String indent(int n) {
		return "\n"+String.format("%"+n+"s", "");
	}
}
