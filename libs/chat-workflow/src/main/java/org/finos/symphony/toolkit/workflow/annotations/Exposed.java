package org.finos.symphony.toolkit.workflow.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Exposed {
	
	public class NoFormClass {}

	/**
	 * A set of patterns that are matched against the user input.
	 * Use '*' to match everything.
	 */
	String[] value();
		
	/**
	 * Used for the help text.  
	 */
	String description() default "";
	
	/**
	 * A list of rooms that the command works in.
	 */
	String[] rooms() default {};
	
	/**
	 * Means that the command can only be done by room administrators.
	 */
	boolean admin() default false;
	
	/**
	 * This means that the exposed method only applies buttons on a given form.
	 * TODO: do we need this?
	 */
	String formName() default ""; 
	
	/**
	 * Says that this method must be represented as a button when we display objects of 
	 * this class.
	 **/
	Class<?> formClass() default NoFormClass.class;
	
	boolean addToHelp() default true;
	
	/**
	 * Whether this method can be exposed as a button
	 */
	boolean isButton() default true;
	
	/**
	 * If you want the button text to not match the first value, use this.
	 */
	String buttonText() default "";
	
	/**
	 * Whether this method can be called by typing it's name.
	 */
	boolean isMessage() default true;
	
}
