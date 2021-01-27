package org.finos.symphony.toolkit.workflow.sources.symphony.handlers.freemarker;

import java.lang.reflect.Field;

import org.finos.symphony.toolkit.json.EntityJson;

public abstract class AbstractTableConverter extends AbstractFieldConverter {
	
	

	protected WithField tableColumnNames = (beanClass, f, editMode, variable, ej, ctx) -> {
			String align = numberClass(f.getType()) ? RIGHT_ALIGN : (boolClass(f.getType()) ? CENTER_ALIGN : "");
			return indent(variable.depth+1) + "<td " + align + "><b>" + f.getName() + "</b></td>";
		};

	private boolean numberClass(Class<?> c) {
		return Number.class.isAssignableFrom(c);
	}

	private boolean boolClass(Class<?> c) {
		return (Boolean.class.isAssignableFrom(c)) || (boolean.class.isAssignableFrom(c));
	}

	protected WithField tableDisplay = (beanClass, f, editMode, variable, ej, ctx) -> {
		String align = numberClass(f.getType()) ? RIGHT_ALIGN : (boolClass(f.getType()) ? CENTER_ALIGN : "");
		return indent(variable.depth) + "<td " + align + ">" + ctx.apply(beanClass, f, editMode, variable, ej, ctx) + "</td>";
	};

	public AbstractTableConverter(int priority) {
		super(priority);
	}

	public String createTable(Class<?> beanClass, Field f, boolean editMode, Variable variable, EntityJson ej, WithField headerDetails, WithField rowDetails, WithField ctx) {
		StringBuilder sb = new StringBuilder();
		sb.append(formatErrorsAndIndent(variable));
		sb.append(indent(variable.depth) + "<table><thead><tr>");
		sb.append(headerDetails.apply(beanClass, f, editMode, variable, ej, ctx));
		sb.append(indent(variable.depth) + "</tr></thead><tbody>");
		sb.append(rowDetails.apply(beanClass, f, editMode, variable, ej, ctx));
		sb.append(indent(variable.depth) + "</tbody></table>");
		return sb.toString();
		
	}
	
}