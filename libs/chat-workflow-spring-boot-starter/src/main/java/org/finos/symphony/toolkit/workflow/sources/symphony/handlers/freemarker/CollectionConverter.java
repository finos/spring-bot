package org.finos.symphony.toolkit.workflow.sources.symphony.handlers.freemarker;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;

import org.finos.symphony.toolkit.json.EntityJson;
import org.finos.symphony.toolkit.workflow.sources.symphony.elements.edit.TableAddRow;
import org.finos.symphony.toolkit.workflow.sources.symphony.elements.edit.TableDeleteRows;
import org.finos.symphony.toolkit.workflow.sources.symphony.elements.edit.TableEditRow;

public class CollectionConverter extends AbstractTableConverter {
	
	public CollectionConverter() {
		super(LOW_PRIORITY);
	}
		
	@Override
	public boolean canConvert(Type t) {
		if (t instanceof ParameterizedType) {
			Type rawType = ((ParameterizedType)t).getRawType();
			return (rawType instanceof Class<?>) && Collection.class.isAssignableFrom((Class<?>) rawType);
		} else {
			return false;
		}
	}

	@Override
	public String apply(WithType controller, Type t, boolean editMode, Variable variable, EntityJson ej, WithField showDetail) {
		if (showDetail.expand()) {
			return createTable(t, editMode, variable, ej, tableColumnNames(), tableColumnValues(), controller);
		} else {
			return text(variable, "!''");
		}
	}
	
	
	
	@Override
	protected Object rowDetails(Type t, boolean editMode, Variable variable, EntityJson ej, WithField cellDetail, WithType controller) {
		Class<?> elementClass = (Class<?>) ((ParameterizedType) t).getActualTypeArguments()[0];
		
		TypeConverter elementTypeConverter = controller.getConverter(elementClass, controller);
		
		StringBuilder sb = new StringBuilder();
		Variable subVar = variable.index();

		// handle each field
		sb.append(beginIterator(variable, subVar));
		sb.append(indent(subVar.depth) + "<tr>");
		
		if (elementTypeConverter instanceof SimpleTypeConverter) {
			sb.append(((SimpleTypeConverter)elementTypeConverter).apply(controller, elementClass, false, subVar, ej, cellDetail));
		} else if (elementTypeConverter instanceof ComplexTypeConverter) {
			sb.append(((ComplexTypeConverter)elementTypeConverter).withFields(controller, elementClass, false, subVar, ej, cellDetail));
		} else {
			throw new UnsupportedOperationException();
		}
		
		
		if (editMode) {
			sb.append(indent(subVar.depth+1) + "<td " + CENTER_AND_WIDTH_ALIGN + "><checkbox name=\""+ variable.getFormFieldName() + ".${" + subVar.getDataPath() + "?index}." + TableDeleteRows.SELECT_SUFFIX + "\" /></td>");
			sb.append(indent(subVar.depth+1) + "<td " + CENTER_ALIGN + "><button name=\"" + variable.getFormFieldName() + "[${" + subVar.getDataPath() + "?index}]." + TableEditRow.EDIT_SUFFIX + "\">Edit</button></td>");
		}

		sb.append(indent(subVar.depth) + "</tr>");
		sb.append(endIterator(variable));
		return sb.toString();
	}

	@Override
	protected Object rowHeaders(Type t, boolean editMode, Variable variable, EntityJson ej, WithField cellDetail, WithType controller) {
		Class<?> elementClass = (Class<?>) ((ParameterizedType) t).getActualTypeArguments()[0];
		TypeConverter elementTypeConverter = controller.getConverter(elementClass, controller); 
		
		StringBuilder sb = new StringBuilder();

		if (elementTypeConverter instanceof SimpleTypeConverter) {
			sb.append("<td>Value</td>");
		} else if (elementTypeConverter instanceof ComplexTypeConverter) {
			sb.append(((ComplexTypeConverter)elementTypeConverter).withFields(controller, elementClass, editMode, variable, ej, cellDetail));
		} else {
			throw new UnsupportedOperationException();
		}
		
		if (editMode) {
			sb.append(indent(variable.depth+1) + "<td " + CENTER_ALIGN + "><button name=\"" + variable.getFormFieldName() + "." + TableDeleteRows.ACTION_SUFFIX
					+ "\">Delete</button></td>");
			sb.append(indent(variable.depth+1)+ "<td " + CENTER_ALIGN + "><button name=\"" + variable.getFormFieldName() + "." + TableAddRow.ACTION_SUFFIX
					+ "\">New</button></td>");
		}
		
		return sb.toString();
	};

}
