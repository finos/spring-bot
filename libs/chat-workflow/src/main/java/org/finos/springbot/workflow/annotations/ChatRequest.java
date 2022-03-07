package org.finos.springbot.workflow.annotations;

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
	 * A list of rooms which are excluded that the command works in.
	 */
	String[] excludeRooms() default {};
	
	/**
	 * Means that the command can only be done by room administrators.
	 */
	boolean admin() default false;
	
	/**
	 * Whether or not to show this command on the help menu
	 */
	boolean addToHelp() default true;

	/**
	 * Position of this command on the help menu
	 */
	int helpOrder() default Integer.MAX_VALUE;

	/**
	 * Whether this command can be exposed as a button Help Page
	 * @return
	 */
	boolean isButtonOnHelpPage() default true;
	
}
