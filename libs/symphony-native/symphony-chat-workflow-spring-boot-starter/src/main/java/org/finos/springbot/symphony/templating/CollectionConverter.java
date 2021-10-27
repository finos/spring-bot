package org.finos.springbot.symphony.templating;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;

import org.finos.springbot.workflow.actions.form.TableAddRow;
import org.finos.springbot.workflow.actions.form.TableDeleteRows;
import org.finos.springbot.workflow.actions.form.TableEditRow;
import org.finos.springbot.workflow.templating.AbstractTableConverter;
import org.finos.springbot.workflow.templating.ComplexTypeConverter;
import org.finos.springbot.workflow.templating.SimpleTypeConverter;
import org.finos.springbot.workflow.templating.TypeConverter;
import org.finos.springbot.workflow.templating.Variable;
import org.finos.springbot.workflow.templating.WithField;
import org.finos.springbot.workflow.templating.WithType;

public class CollectionConverter extends AbstractTableConverter<String> {
	
	public CollectionConverter() {
		super(LOW_PRIORITY);
	}
		
	@Override
	public boolean canConvert(Field ctx, Type t) {
		if (t instanceof ParameterizedType) {
			Type rawType = ((ParameterizedType)t).getRawType();
			return (rawType instanceof Class<?>) && Collection.class.isAssignableFrom((Class<?>) rawType);
		} else {
			return false;
		}
	}

	@Override
	public String apply(Field ctx, WithType controller, Type t, boolean editMode, Variable variable, WithField showDetail) {
		if (null == showDetail) return "...";
		if (showDetail.expand()) {
			return createTable(t, editMode, variable, tableColumnNames(), tableColumnValues(), controller);
		} else {
			return text(variable, "!''");
		}
	}
	
	
	
	@Override
	protected Object rowDetails(Type t, boolean editMode, Variable variable, WithField cellDetail, WithType controller) {
		Class<?> elementClass = (Class<?>) ((ParameterizedType) t).getActualTypeArguments()[0];

		TypeConverter elementTypeConverter = controller.getConverter(null, elementClass, controller);

		StringBuilder sb = new StringBuilder();
		Variable subVar = variable.index();

		// handle each field
		sb.append(beginIterator(variable, subVar));
		sb.append(indent(subVar.depth) + "<tr>");

		if (elementTypeConverter instanceof SimpleTypeConverter) {
			sb.append("<td>");
			sb.append(((SimpleTypeConverter)elementTypeConverter).apply(null, controller, elementClass, false, subVar, cellDetail));
			sb.append("</td>");
		} else if (elementTypeConverter instanceof ComplexTypeConverter) {
			sb.append(((ComplexTypeConverter)elementTypeConverter).withFields(controller, elementClass, false, subVar, cellDetail));
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
	protected Object rowHeaders(Type t, boolean editMode, Variable variable, WithField cellDetail, WithType controller) {
		Class<?> elementClass = (Class<?>) ((ParameterizedType) t).getActualTypeArguments()[0];
		TypeConverter elementTypeConverter = controller.getConverter(null, elementClass, controller);

		StringBuilder sb = new StringBuilder();

		if (elementTypeConverter instanceof SimpleTypeConverter) {
			sb.append("<td><b>Value</b></td>");
		} else if (elementTypeConverter instanceof ComplexTypeConverter) {
			sb.append(((ComplexTypeConverter)elementTypeConverter).withFields(controller, elementClass, editMode, variable, cellDetail));
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
	}

}
