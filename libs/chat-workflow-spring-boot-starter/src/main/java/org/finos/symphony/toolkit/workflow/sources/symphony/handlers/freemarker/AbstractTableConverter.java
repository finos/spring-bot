package org.finos.symphony.toolkit.workflow.sources.symphony.handlers.freemarker;

import org.finos.symphony.toolkit.json.EntityJson;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

public abstract class AbstractTableConverter extends AbstractComplexTypeConverter {


    protected WithField tableColumnNames() {
        return new WithField() {

            @Override
            public boolean expand() {
                return false;
            }

            @Override
            public String apply(Field f, boolean editMode, Variable variable, EntityJson ej, WithType contentHandler) {
                String align = numberClass(f.getType()) ? RIGHT_ALIGN : (boolClass(f.getType()) ? CENTER_ALIGN : CENTER_AND_WIDTH_ALIGN);
                String fieldNameOrientation = getFieldNameOrientation(f);
                return StringUtils.hasText(fieldNameOrientation) ? indent(variable.depth+1) + "<td " + align + "><b>" + fieldNameOrientation + "</b></td>" : "";
            }
        };
    }

    private boolean numberClass(Class<?> c) {
        return Number.class.isAssignableFrom(c);
    }

    private boolean boolClass(Class<?> c) {
        return (Boolean.class.isAssignableFrom(c)) || (boolean.class.isAssignableFrom(c));
    }

    protected WithField tableColumnValues() {
        return new WithField() {

            @Override
            public boolean expand() {
                return false;
            }

            @Override
            public String apply(Field f, boolean editMode, Variable variable, EntityJson ej, WithType contentHandler) {
                String align = numberClass(f.getType()) ? RIGHT_ALIGN : (boolClass(f.getType()) ? CENTER_ALIGN : "");
                Type t = f.getGenericType();
                return StringUtils.hasText(getFieldNameOrientation(f))? indent(variable.depth) + "<td " + align + ">" + contentHandler.apply(contentHandler, t, editMode, variable, ej, null) + "</td>" : "";
            }
        };

    }

    ;

    public AbstractTableConverter(int priority) {
        super(priority);
    }

    public String createTable(Type t, boolean editMode, Variable variable, EntityJson ej, WithField headerDetail, WithField rowDetail, WithType controller) {
        StringBuilder sb = new StringBuilder();
        sb.append(formatErrorsAndIndent(variable));
        sb.append(indent(variable.depth) + "<table><thead><tr>");
        sb.append(rowHeaders(t, editMode, variable, ej, headerDetail, controller));
        sb.append(indent(variable.depth) + "</tr></thead><tbody>");
        sb.append(rowDetails(t, editMode, variable, ej, rowDetail, controller));
        sb.append(indent(variable.depth) + "</tbody></table>");
        return sb.toString();

    }

    protected abstract Object rowDetails(Type t, boolean editMode, Variable variable, EntityJson ej, WithField rowDetail, WithType controller);

    protected abstract Object rowHeaders(Type t, boolean editMode, Variable variable, EntityJson ej, WithField headerDetails, WithType controller);

}