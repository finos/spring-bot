package org.finos.symphony.toolkit.workflow.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.finos.symphony.toolkit.workflow.response.handlers.ResponseHandler;

/** 
 * Annotate a class with this to give a default template name to use for the {@link ResponseHandler}.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Template {

	public String view() default "";
	
	public String edit() default "";
}
