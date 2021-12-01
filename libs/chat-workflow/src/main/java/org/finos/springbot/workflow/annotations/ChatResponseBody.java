package org.finos.springbot.workflow.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Add this annotation on controller methods that return an object 
 * to indicate whether the object should be returned in edit or view mode,
 * and (optionally) the name of the template to use.
 * 
 * @author rob@kite9.com
 *
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface ChatResponseBody {
	
	/**
	 * Override this to allow
	 */
	WorkMode workMode() default WorkMode.VIEW;
	
	String template() default "";
	
}
