package org.finos.symphony.toolkit.workflow.sources.symphony.handlers.freemarker.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface DisplayAttribute {
    public final static String DEFAULT_NAME = "";

    String name() default DEFAULT_NAME;
}
