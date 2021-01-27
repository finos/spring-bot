package org.finos.symphony.toolkit.workflow.sources.symphony.handlers.freemarker;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;

import org.finos.symphony.toolkit.json.EntityJson;
import org.finos.symphony.toolkit.workflow.sources.symphony.elements.edit.TableAddRow;
import org.finos.symphony.toolkit.workflow.sources.symphony.elements.edit.TableDeleteRows;
import org.finos.symphony.toolkit.workflow.sources.symphony.elements.edit.TableEditRow;
import org.springframework.stereotype.Component;

@Component
public class CollectionConverter extends AbstractTableConverter {
	
	public CollectionConverter() {
		super(LOW_PRIORITY);
	}
		
	@Override
	public boolean canConvert(Field f) {
		return Collection.class.isAssignableFrom(f.getType());
	}

	@Override
	public String apply(Class<?> beanClass, Field f, boolean editMode, Variable variable, EntityJson ej, WithField ctx) {
		return createTable(beanClass, f, editMode, variable, ej, headerDetails, rowDetails, ctx);
	}
	
	private WithField rowDetails = (beanClass, f, editMode, variable, ej, ctx) -> {
		Class<?> elementClass = (Class<?>) ((ParameterizedType) f.getGenericType()).getActualTypeArguments()[0];
		StringBuilder sb = new StringBuilder();
		Variable subVar = variable.index();

		// handle each field
		sb.append(beginIterator(variable, subVar));
		sb.append(indent(subVar.depth) + "<tr>");
		sb.append(ctx.apply(elementClass, null, editMode, subVar, ej, tableDisplay));
		
		if (editMode) {
			sb.append(indent(subVar.depth+1) + "<td " + CENTER_ALIGN + "><checkbox name=\""+ variable.getFormFieldName() + ".${" + subVar.getDataPath() + "?index}." + TableDeleteRows.SELECT_SUFFIX + "\" /></td>");
			sb.append(indent(subVar.depth+1) + "<td " + CENTER_ALIGN + "><button name=\"" + variable.getFormFieldName() + "[${" + subVar.getDataPath() + "?index}]." + TableEditRow.EDIT_SUFFIX + "\">Edit</button></td>");
		}

		sb.append(indent(subVar.depth) + "</tr>");
		sb.append(endIterator(variable));
		return sb.toString();
	};


	private WithField headerDetails = (beanClass, f, editMode, variable, ej, ctx) -> { 
		Class<?> elementClass = (Class<?>) ((ParameterizedType) f.getGenericType()).getActualTypeArguments()[0];
		StringBuilder sb = new StringBuilder();
		sb.append(ctx.apply(elementClass, null, editMode, variable, ej, tableColumnNames));
		if (editMode) {
			sb.append(indent(variable.depth+1) + "<td " + CENTER_ALIGN + "><button name=\"" + variable.getFormFieldName() + "." + TableDeleteRows.ACTION_SUFFIX
					+ "\">Delete</button></td>");
			sb.append(indent(variable.depth+1)+ "<td " + CENTER_ALIGN + "><button name=\"" + variable.getFormFieldName() + "." + TableAddRow.ACTION_SUFFIX
					+ "\">New</button></td>");
		}
		
		return sb.toString();
	};

}
