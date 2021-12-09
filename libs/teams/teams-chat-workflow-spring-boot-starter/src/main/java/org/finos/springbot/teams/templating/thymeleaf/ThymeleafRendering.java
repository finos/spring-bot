package org.finos.springbot.teams.templating.thymeleaf;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.finos.springbot.workflow.templating.Rendering;
import org.finos.springbot.workflow.templating.Variable;
import org.springframework.util.StringUtils;


public class ThymeleafRendering implements Rendering<String> {

	@Override
	public String description(String d) {
		return new String("<span>"+d+"</span>");
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
		int indent = ((ThymeleafVariable) variable).depth;

		return indent(indent)
				+ "  <div th:each=\""+index+" : ${"+choiceLocation+"}\">"
				+ indent(indent)
				+ "   <span th:if=\"${"+index+extend(choiceKey)+"} == ${"+variable.getDataPath()+extend(variableKey)+"}\""
				+ " th:text=\"${"+index+extend(choiceValue)+"}\">choice</span>"
				+ "  </div>";		
	}
	
	private String mapToIfs(Map<String, String> options, Variable v, String variableKey) {
		return options.entrySet().stream()
			.map(e -> convertToIf(e, v, variableKey))
			.reduce((a, b) -> a + "\n"+ b)
			.orElse("");

	}
	
	private String convertToIf(Entry<String, String> e, Variable v, String variableKey) {
		return "   <span th:if=\"${"+v.getDataPath()+variableKey+"?.name} == '"+e.getKey()+"'\">"  	
				+ e.getValue()+"</span>";
	}

	@Override
	public String renderDropdown(Variable variable, String variableKey, Map<String, String> options, boolean editable) {
		int indent = ((ThymeleafVariable) variable).depth;
		return indent(indent) + mapToIfs(options, variable, variableKey);
	}

	@Override
	public String textField(Variable variable, boolean editable) {
		return text(variable,"");
	}

	@Override
	public String checkBox(Variable variable, boolean editable) {
		return "<span th:text=\"${"+variable.getDataPath()+" ? 'Y' : 'N'}\">boolean</span>";
	}

	@Override
	public String collection(Type t, Variable v, Variable i, String in, boolean editable) {
		return "<div th:each=\""+i.getDataPath()+" : ${"+v.getDataPath()+"}\">" + in + "</div>";
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

	private String text(Variable variable, String extension) {
		String data = variable.getDataPath() + extension;
		return new String("<span th:text=\"${"+data+"}\">text</span>");
	}
	
	private String indent(int n) {
		return "\n"+String.format("%"+n+"s", "");
	}

	@Override
	public String table(Variable variable, String headers, String body) {
		 StringBuilder sb = new StringBuilder();
	     int depth = ((ThymeleafVariable) variable).depth;
	     sb.append(indent(depth) + "<table><thead><tr>");
	     sb.append(headers);
	     sb.append(indent(depth) + "</tr></thead><tbody>");
	     sb.append(body);
	     sb.append(indent(depth) + "</tbody></table>");
	     return sb.toString();
	} 

	@Override
	public String userDisplay(Variable v) {
		return "<at th:key=\"${"+v.getDataPath()+"?.key}\" th:text=\"${"+v.getDataPath()+"?.name}\">User Name</at>";
	}
}
