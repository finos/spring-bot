package org.finos.symphony.toolkit.workflow.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Allows controller methods to be bound to messages sent to the bot via chat.
 * 
 * @author rob@kite9.com
 *
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface ChatRequest {

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
	 * Whether or not to show this command on the help menu
	 */
	boolean addToHelp() default true;
	
}
