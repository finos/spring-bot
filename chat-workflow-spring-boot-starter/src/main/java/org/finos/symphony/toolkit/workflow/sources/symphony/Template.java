package org.finos.symphony.toolkit.workflow.sources.symphony;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Template {

	public String view() default "";
	
	public String edit() default "";
}
