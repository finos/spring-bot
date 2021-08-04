package org.finos.symphony.toolkit.workflow.annotations;

/**
 * Add this annotation on controller methods that return an object 
 * to indicate whether the object should be returned in edit or view mode,
 * and (optionally) the name of the template to use.
 * 
 * @author rob@kite9.com
 *
 */
public @interface ChatResponseBody {
	
	/**
	 * When you annotate a controller method with 
	 */
	WorkMode workMode() default WorkMode.VIEW;
	
	String template() default "";
}
