package org.finos.symphony.toolkit.workflow.sources.symphony.handlers.freemarker;

import static java.util.Optional.ofNullable;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

import org.finos.symphony.toolkit.workflow.sources.symphony.handlers.freemarker.annotations.Display;

/**
 * General interface for performing some function against a field, with a given variable.
 */
public interface WithField {
	
    String DEFAULT_FORMATTER_PATTERN = "(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])";

    public String apply(Field f, boolean editMode, Variable variable, WithType contentHandler);

    /**
     * Return true if we are going to expand the contents of this field.
     */
    public boolean expand();

    default String getFieldNameOrientation(Field f) {
        return ofNullable(f.getAnnotation(Display.class)).map(display -> {
            if (display.visible()) {
                return !display.name().trim().isEmpty() ? display.name() : fieldNameDefaultFormatter(f.getName());
            } else {
                return "";
            }
        }).orElse(fieldNameDefaultFormatter(f.getName()));
    }

    default String fieldNameDefaultFormatter(String fieldName) {
        return Arrays.stream(Optional.ofNullable(fieldName).orElse("").split(DEFAULT_FORMATTER_PATTERN))
                .map(word -> {
                    return null != word && !word.trim().isEmpty() ? Character.toUpperCase(word.charAt(0)) + word.substring(1) : "";
                })
                .collect(Collectors.joining(" "));
    }
}
