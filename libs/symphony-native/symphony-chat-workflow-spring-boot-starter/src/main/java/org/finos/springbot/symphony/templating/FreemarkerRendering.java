package org.finos.springbot.symphony.templating;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.finos.springbot.entityjson.EntityJson;
import org.finos.springbot.workflow.actions.form.TableDeleteRows;
import org.finos.springbot.workflow.actions.form.TableEditRow;
import org.finos.springbot.workflow.templating.TableRendering;
import org.finos.springbot.workflow.templating.Variable;
import org.springframework.util.StringUtils;

import com.symphony.user.UserId;
import com.symphony.user.EmailAddress;

public class FreemarkerRendering implements TableRendering<String> {

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
	public String renderUserDropdown(Variable variable, String optionLocation, String optionKey, String optionValue,
			boolean editable) {
		return renderDropdown(variable, "id[0].value", optionLocation, optionKey, optionValue, editable);
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
					+ "   <option value=\"${"+index+extend(choiceKey)+"}\""
					+ " selected=\"${((("+variable.getDataPath()+extend(variableKey)+")!'') == "+index+extend(choiceKey)+")?string('true','false')}\""
					+ ">"
					+ indent(indent)
					+ "    ${"+index+extend(choiceValue)+"}"
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
					+ "   <#if ("+index+extend(choiceKey)+" == ("+variable.getDataPath()+extend(variableKey)+")!'')>"
					+ indent(indent)
					+ "    ${"+index+extend(choiceValue)+"}"
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
	public String collection(Type t, Variable v, Variable i, String in, boolean editable) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String button(String text, String id) {
		return "<button name=\"" + id + "\">"+text+"</button>";
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

	@Override
	public String table(Variable variable, String headers, String body) {
		 StringBuilder sb = new StringBuilder();
	     int depth = variable.getDepth();
		 sb.append(formatErrorsAndIndent(variable.getFormFieldName(), depth));
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
	
	protected String beginIterator(Variable variable, Variable reg) {
		return indent(variable) + "<#list "+variable.getDataPath()+" as "+reg.getDataPath()+">";
	}

	protected String indent(Variable variable) {
		return indent(variable.getDepth());
	}
	
	protected String endIterator(Variable variable) {
		return indent(variable) + "</#list>";
	}
	
	@Override
	public String tableRow(Variable variable, Variable subVar, List<String> cells) {
		StringBuilder sb = new StringBuilder();
		sb.append(beginIterator(variable, subVar));
		sb.append(indent(subVar) + " <tr>");
		sb.append(cells.stream().reduce((a, b) -> a + "\n" + b).orElse(""));
		sb.append(indent(subVar) + "</tr>");
		sb.append(endIterator(variable));
		return sb.toString();
	}
	
	@Override
	public String tableHeaderRow(List<String> contents) {
		return contents.stream().reduce((a, b) -> a + "\n" + b).orElse("");
	}

	
	@Override
	public String tableRowCheckBox(Variable variable, Variable subVar) {
		return "<checkbox name=\""+ variable.getFormFieldName() + ".${" + subVar.getDataPath() + "?index}." + TableDeleteRows.SELECT_SUFFIX + "\" />";
	}

	@Override
	public String tableRowEditButton(Variable variable, Variable subVar) {
		String editId = variable.getFormFieldName() + "[${" + subVar.getDataPath() + "?index}]." + TableEditRow.EDIT_SUFFIX;
		return button("Edit", editId);
	}

	@Override
	public String userDisplay(Variable v) {
		StringBuilder sb = new StringBuilder();
	    int depth = ((FreemarkerVariable) v).depth;
		sb.append(indent(depth) + "<#if "+v.getDataPath() + "??><#list "+v.getDataPath() +".id as id>");
		sb.append(indent(depth) + " <#if id??>");
		sb.append(indent(depth) + " <#if id.type == '"+EntityJson.getEntityJsonTypeName(UserId.class)+"'><mention uid=\"${id.value}\" /><#break></#if>");
		sb.append(indent(depth) + " <#if id.type == '"+EntityJson.getEntityJsonTypeName(EmailAddress.class)+"'><mention email=\"${id.value}\" /><#break></#if>");
	    sb.append(indent(depth) + " </#if></#list></#if>");
	    return sb.toString();
	}
}
