package org.finos.springbot.sources.teams.handlers.adaptivecard;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Optional;

import org.finos.springbot.workflow.templating.AbstractComplexTypeConverter;
import org.finos.springbot.workflow.templating.Variable;
import org.finos.springbot.workflow.templating.WithField;
import org.finos.springbot.workflow.templating.WithType;
import org.springframework.util.StringUtils;

public abstract class AbstractTableConverter<X> extends AbstractComplexTypeConverter<X> {


    protected WithField<X> tableColumnNames() {
        return new WithField() {

            @Override
            public boolean expand() {
                return false;
            }

            @Override
            public String apply(Field f, boolean editMode, Variable variable, WithType contentHandler) {
                String align = numberClass(f.getType()) ? RIGHT_ALIGN : boolClass(f.getType()) ? CENTER_ALIGN : "";
                String fieldNameOrientation = getFieldNameOrientation(f);
                return StringUtils.hasText(fieldNameOrientation) ? indent(variable.depth + 1) + Optional.ofNullable(align).map(style -> StringUtils.hasText(style) ? "<td " + style + "><b>" : "<td><b>").get() + fieldNameOrientation + "</b></td>" : "";
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
            public String apply(Field f, boolean editMode, Variable variable, WithType contentHandler) {
                String align = numberClass(f.getType()) ? RIGHT_ALIGN : (boolClass(f.getType()) ? CENTER_ALIGN : "");
                Type t = f.getGenericType();
                return StringUtils.hasText(getFieldNameOrientation(f)) ? indent(variable.depth) + "<td " + align + ">" + contentHandler.apply(null, contentHandler, t, editMode, variable, null) + "</td>" : "";
            }
        };

    }

    ;

    public AbstractTableConverter(int priority) {
        super(priority);
    }

    public String createTable(Type t, boolean editMode, Variable variable, WithField headerDetail, WithField rowDetail, WithType controller) {
        StringBuilder sb = new StringBuilder();
        sb.append(formatErrorsAndIndent(variable.getFormFieldName(), variable.depth));
        sb.append(indent(variable.depth) + "<table><thead><tr>");
        sb.append(rowHeaders(t, editMode, variable, headerDetail, controller));
        sb.append(indent(variable.depth) + "</tr></thead><tbody>");
        sb.append(rowDetails(t, editMode, variable, rowDetail, controller));
        sb.append(indent(variable.depth) + "</tbody></table>");
        return sb.toString();

    }

    protected abstract Object rowDetails(Type t, boolean editMode, Variable variable, WithField rowDetail, WithType controller);

    protected abstract Object rowHeaders(Type t, boolean editMode, Variable variable,WithField headerDetails, WithType controller);

}