package org.finos.springbot.workflow.templating;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.finos.springbot.workflow.actions.form.TableAddRow;
import org.finos.springbot.workflow.actions.form.TableDeleteRows;
import org.springframework.util.StringUtils;

public class TableConverter<X> extends AbstractTableConverter<X> {
	
	public TableConverter(TableRendering<X> r) {
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
	public X apply(Field ctx, WithType<X> controller, Type t, boolean editMode, Variable variable, WithField<X> showDetail) {
		if (null == showDetail) return getR().description("...");
		if (showDetail.expand()) {
			return createTable(t, editMode, variable, tableColumnNames(), tableColumnValues(), controller);
		} else {
			return getR().textField(variable, false);
		}
	}

	
	@Override
	protected X rowDetails(Type t, boolean editMode, Variable variable, WithField<X> cellDetail, WithType<X> controller) {
		Class<?> elementClass = (Class<?>) ((ParameterizedType) t).getActualTypeArguments()[0];

		TypeConverter<X> elementTypeConverter = controller.getConverter(null, elementClass, controller);

		Variable subVar = variable.index();

		
		List<X> out = new ArrayList<X>();

		if (elementTypeConverter instanceof SimpleTypeConverter) {
			X cellContent = ((SimpleTypeConverter<X>)elementTypeConverter).apply(null, controller, elementClass, false, subVar, cellDetail);
			out.add(cellContent);
		} else if (elementTypeConverter instanceof ComplexTypeConverter) {
			List<X> cellContents = ((ComplexTypeConverter<X>)elementTypeConverter).withFields(controller, elementClass, false, subVar, cellDetail);
			out.addAll(cellContents);
		} else {
			throw new UnsupportedOperationException();
		}

		
		if (editMode) {
			X checkbox = getR().tableRowCheckBox(variable, subVar);
			out.add(getR().tableCell(CENTER_AND_WIDTH_ALIGN, checkbox));
			X editButton = getR().tableRowEditButton(variable, subVar);
			out.add(getR().tableCell(CENTER_ALIGN, editButton));
		}
		
		
		X row = getR().tableRow(variable, subVar, out);
		
		return row;
	}

	@Override
	protected X rowHeaders(Type t, boolean editMode, Variable variable, WithField<X> cellDetail, WithType<X> controller) {
		Class<?> elementClass = (Class<?>) ((ParameterizedType) t).getActualTypeArguments()[0];
		TypeConverter<X> elementTypeConverter = controller.getConverter(null, elementClass, controller);
		
		List<X> out = new ArrayList<X>();
 
		if (elementTypeConverter instanceof SimpleTypeConverter) {
			out.add(getR().description("Value"));
		} else if (elementTypeConverter instanceof ComplexTypeConverter) {
			out.addAll(((ComplexTypeConverter<X>)elementTypeConverter).withFields(controller, elementClass, editMode, variable, cellDetail));
		} else {
			throw new UnsupportedOperationException();
		}

		if (editMode) {
			String deleteId = variable.getFormFieldName() + "." + TableDeleteRows.ACTION_SUFFIX;
			out.add(getR().tableCell(CENTER_ALIGN, getR().button("Delete", deleteId)));
			String newId = variable.getFormFieldName() + "." + TableAddRow.ACTION_SUFFIX;
			out.add(getR().tableCell(CENTER_ALIGN, getR().button("New", newId)));
		}
		
		return getR().tableHeaderRow(out);
	}


    private boolean numberClass(Class<?> c) {
        return Number.class.isAssignableFrom(c);
    }

    private boolean boolClass(Class<?> c) {
        return (Boolean.class.isAssignableFrom(c)) || (boolean.class.isAssignableFrom(c));
    }

    protected WithField<X> tableColumnValues() {
        return new WithField<X>() {

            @Override
            public boolean expand() {
                return false;
            }

            @Override
            public X apply(Field f, boolean editMode, Variable variable, WithType<X> contentHandler) {
                Map<String, String> align = numberClass(f.getType()) ? RIGHT_ALIGN : (boolClass(f.getType()) ? CENTER_ALIGN : Collections.emptyMap());
                Type t = f.getGenericType();
                return StringUtils.hasText(getFieldNameOrientation(f)) ? getR().tableCell(
                		align, contentHandler.apply(null, contentHandler, t, editMode, variable, null))
                	: getR().description("");
            }
        };

    }


    protected WithField<X> tableColumnNames() {
        return new WithField<X>() {

            @Override
            public boolean expand() {
                return false;
            }

            @Override
            public X apply(Field f, boolean editMode, Variable variable, WithType<X> contentHandler) {
            	Map<String, String> align = numberClass(f.getType()) ? RIGHT_ALIGN : boolClass(f.getType()) ? CENTER_ALIGN : Collections.emptyMap();
                String fieldNameOrientation = getFieldNameOrientation(f);
                return getR().tableCell(align, getR().description(fieldNameOrientation == null ? "" : fieldNameOrientation));
            }
        };
    }
  
}
