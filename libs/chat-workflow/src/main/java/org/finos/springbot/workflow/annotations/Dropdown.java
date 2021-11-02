package org.finos.springbot.workflow.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Mark a field with this interface to allow it to use dropdown options.
 * 
 * @author rob@kite9.com
 *
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Dropdown {
	
	/**
	 * Dictates where in the JSON structure to get the options for the dropdown
	 */
	public String data() default "options.contents";
	
	public String key() default ".key";
	
	public String name() default ".name";

}
