package org.finos.springbot.teams.templating.thymeleaf;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.finos.springbot.workflow.templating.TableRendering;
import org.finos.springbot.workflow.templating.Variable;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.JsonNode;


public class ThymeleafRendering implements TableRendering<String> {

	@Override
	public String description(String d) {
		return "<span>"+d+"</span>";
	}

	@Override
	public String list(List<String> contents) {
		return "<table>" + contents.stream().reduce((a, b) -> a + "\n" + b).orElse("") + "</table>";
	}

	@Override
	public String addFieldName(String field, String value) {
		return !StringUtils.hasText(field) ? "" : "<tr><td style=\"width: 200px\"><b>" + field + ":</b></td><td>" + value + "</td></tr>";
	}

	@Override
	public String renderDropdown(Variable variable, String variableKey, String choiceLocation, String choiceKey, String choiceValue, boolean editable) {
		Variable index = variable.index();
		String indexName = index.getDataPath();
		int indent = ((ThymeleafVariable) variable).depth;

		return indent(indent)
				+ "  <div th:each=\""+indexName+" : ${"+choiceLocation+"}\">"
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
	public String renderUserDropdown(Variable variable, String optionLocation, String optionKey, String optionValue,
			boolean editable) {
		return renderDropdown(variable, "key", optionLocation, optionKey, optionValue, editable);
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
	     int depth = variable.getDepth();
	     sb.append(indent(depth) + "<table><thead><tr>");
	     sb.append(headers);
	     sb.append(indent(depth) + "</tr></thead><tbody>");
	     sb.append(body);
	     sb.append(indent(depth) + "</tbody></table>");
	     return sb.toString();
	}
	
	@Override
	public String tableCell(Map<String, String> attributes, String content) {
		String atts = attributes.entrySet().stream()
			.map(e -> e.getKey()+"=\""+e.getValue()+"\"")
			.reduce("", (a, b) -> a+" "+b)
			.trim();
		atts = atts.length() > 0 ? " "+atts : atts;

		return "<td"+atts+">"+content+"</td>";
	}
	
	protected String indent(Variable variable) {
		return indent(variable.getDepth());
	}
	
	@Override
	public String tableRow(Variable variable, Variable subVar, List<String> cells) {
		StringBuilder sb = new StringBuilder();
		String indexName = subVar.getDataPath();
		String choiceLocation = variable.getDataPath();
		sb.append(indent(subVar));
		sb.append("<tr th:each=\""+indexName+" : ${"+choiceLocation+"}\">");
		sb.append(cells.stream().reduce((a, b) -> a + "\n" + b).orElse(""));
		sb.append(indent(subVar) + "</tr>");
		return sb.toString();
	}
	
	@Override
	public String tableHeaderRow(List<String> contents) {
		return contents.stream().reduce((a, b) -> a + "\n" + b).orElse("");
	}

	@Override
	public String tableRowCheckBox(Variable variable, Variable r) {
		throw new UnsupportedOperationException("Teams can't do edit mode");
	}
	
	@Override
	public String tableRowEditButton(Variable variable, Variable subVar) {
		throw new UnsupportedOperationException("Teams can't do edit mode");
	}

	@Override
	public String userDisplay(Variable v) {
		return "<at th:key=\"${"+v.getDataPath()+"?.key}\" th:text=\"${"+v.getDataPath()+"?.name}\">User Name</at>";
	}
}
