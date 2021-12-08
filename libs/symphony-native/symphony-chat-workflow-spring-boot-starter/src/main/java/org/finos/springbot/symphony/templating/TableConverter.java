package org.finos.springbot.symphony.templating;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.finos.springbot.workflow.actions.form.TableAddRow;
import org.finos.springbot.workflow.actions.form.TableDeleteRows;
import org.finos.springbot.workflow.actions.form.TableEditRow;
import org.finos.springbot.workflow.templating.AbstractTableConverter;
import org.finos.springbot.workflow.templating.ComplexTypeConverter;
import org.finos.springbot.workflow.templating.Rendering;
import org.finos.springbot.workflow.templating.SimpleTypeConverter;
import org.finos.springbot.workflow.templating.TypeConverter;
import org.finos.springbot.workflow.templating.Variable;
import org.finos.springbot.workflow.templating.WithField;
import org.finos.springbot.workflow.templating.WithType;
import org.springframework.util.StringUtils;

public class TableConverter extends AbstractTableConverter<String> {
	
	public TableConverter(Rendering<String> r) {
		super(LOW_PRIORITY, r);
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
	public String apply(Field ctx, WithType<String> controller, Type t, boolean editMode, Variable variable, WithField<String> showDetail) {
		if (null == showDetail) return "...";
		if (showDetail.expand()) {
			return createTable(t, editMode, variable, tableColumnNames(), tableColumnValues(), controller);
		} else {
			return r.textField(variable, false);
		}
	}
	

	public static String beginIterator(Variable variable, Variable reg) {
		return indent(variable) + "<#list "+variable.getDataPath()+" as "+reg.getDataPath()+">";
	}

	private static String indent(Variable variable) {
		return indent(((FreemarkerVariable) variable).depth);
	}
	
	public static String endIterator(Variable variable) {
		return indent(variable) + "</#list>";
	}
	
	public static String indent(int n) {
		return "\n"+String.format("%"+n+"s", "");
	}
	
	@Override
	protected String rowDetails(Type t, boolean editMode, Variable variable, WithField<String> cellDetail, WithType<String> controller) {
		Class<?> elementClass = (Class<?>) ((ParameterizedType) t).getActualTypeArguments()[0];

		TypeConverter<String> elementTypeConverter = controller.getConverter(null, elementClass, controller);

		StringBuilder sb = new StringBuilder();
		Variable subVar = variable.index();

		// handle each field
		sb.append(beginIterator(variable, subVar));
		sb.append(indent(subVar) + " <tr>");
		
		List<String> out = new ArrayList<String>();

		if (elementTypeConverter instanceof SimpleTypeConverter) {
			out.add("<td>" + ((SimpleTypeConverter<String>)elementTypeConverter).apply(null, controller, elementClass, false, subVar, cellDetail) + "</td>");
		} else if (elementTypeConverter instanceof ComplexTypeConverter) {
			out.addAll(((ComplexTypeConverter<String>)elementTypeConverter).withFields(controller, elementClass, false, subVar, cellDetail));
		} else {
			throw new UnsupportedOperationException();
		}

		
		if (editMode) {
			out.add(" <td " + CENTER_AND_WIDTH_ALIGN + "><checkbox name=\""+ variable.getFormFieldName() + ".${" + subVar.getDataPath() + "?index}." + TableDeleteRows.SELECT_SUFFIX + "\" /></td>");
			out.add(" <td " + CENTER_ALIGN + "><button name=\"" + variable.getFormFieldName() + "[${" + subVar.getDataPath() + "?index}]." + TableEditRow.EDIT_SUFFIX + "\">Edit</button></td>");
		}
		
		sb.append(out.stream().map(r -> indent(subVar) + r).reduce(String::concat).orElse(""));

		sb.append(indent(subVar) + "</tr>");
		sb.append(endIterator(variable));
		return sb.toString();
	}

	@Override
	protected String rowHeaders(Type t, boolean editMode, Variable variable, WithField<String> cellDetail, WithType<String> controller) {
		Class<?> elementClass = (Class<?>) ((ParameterizedType) t).getActualTypeArguments()[0];
		TypeConverter<String> elementTypeConverter = controller.getConverter(null, elementClass, controller);
		
		List<String> out = new ArrayList<String>();
 
		if (elementTypeConverter instanceof SimpleTypeConverter) {
			out.add("<td><b>Value</b></td>");
		} else if (elementTypeConverter instanceof ComplexTypeConverter) {
			out.addAll(((ComplexTypeConverter<String>)elementTypeConverter).withFields(controller, elementClass, editMode, variable, cellDetail));
		} else {
			throw new UnsupportedOperationException();
		}

		if (editMode) {
			out.add("<td " + CENTER_ALIGN + "><button name=\"" + variable.getFormFieldName() + "." + TableDeleteRows.ACTION_SUFFIX
					+ "\">Delete</button></td>");
			out.add("<td " + CENTER_ALIGN + "><button name=\"" + variable.getFormFieldName() + "." + TableAddRow.ACTION_SUFFIX
					+ "\">New</button></td>");
		}
		
		return out.stream().reduce(String::concat).orElse(CENTER_ALIGN);
	}


    private boolean numberClass(Class<?> c) {
        return Number.class.isAssignableFrom(c);
    }

    private boolean boolClass(Class<?> c) {
        return (Boolean.class.isAssignableFrom(c)) || (boolean.class.isAssignableFrom(c));
    }

    protected WithField<String> tableColumnValues() {
        return new WithField<String>() {

            @Override
            public boolean expand() {
                return false;
            }

            @Override
            public String apply(Field f, boolean editMode, Variable variable, WithType<String> contentHandler) {
                String align = numberClass(f.getType()) ? RIGHT_ALIGN : (boolClass(f.getType()) ? CENTER_ALIGN : "");
                Type t = f.getGenericType();
                return StringUtils.hasText(getFieldNameOrientation(f)) ? "<td " + align + ">" + contentHandler.apply(null, contentHandler, t, editMode, variable, null) + "</td>" : "";
            }
        };

    }


    protected WithField<String> tableColumnNames() {
        return new WithField<String>() {

            @Override
            public boolean expand() {
                return false;
            }

            @Override
            public String apply(Field f, boolean editMode, Variable variable, WithType<String> contentHandler) {
                String align = numberClass(f.getType()) ? RIGHT_ALIGN : boolClass(f.getType()) ? CENTER_ALIGN : "";
                String fieldNameOrientation = getFieldNameOrientation(f);
                return StringUtils.hasText(fieldNameOrientation) ? Optional.ofNullable(align).map(style -> StringUtils.hasText(style) ? "<td " + style + "><b>" : "<td><b>").get() + fieldNameOrientation + "</b></td>" : "";
            }
        };
    }

}
