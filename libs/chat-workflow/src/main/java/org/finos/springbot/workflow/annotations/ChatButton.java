package org.finos.springbot.workflow.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Binds a controller method to a button on an @Work class.
 * 
 * @author rob@kite9.com
 *
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface ChatButton {
	
	/**
	 * This is a java-bean class on which the button will appear.  
	 * That class should be annotated with @Work in order to be part of a chat workflow
	 */
	Class<?> value();

	/**
	 * A list of rooms that the button appears in.
	 */
	String[] rooms() default {};
	
	/**
	 * Means that button can only be actioned by room admins.
	 */
	boolean admin() default false;
	
	/**
	 * Whether this method can be exposed as a button, and if so, whether it appears 
	 * during the formClass's edit mode or display mode.
	 */
	WorkMode showWhen() default WorkMode.BOTH;
	
	/**
	 * If you want the button text to not match the first value, use this.
	 */
	String buttonText();
	
}
