package org.finos.springbot.workflow.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Add this annotation to an @Work-annotated class in order to populate the 
 * response data with a list of users in the current chat.  This can be
 * used for populating a dropdown of the current chat's users. 
 * 
 * You can additionally use this annotation on a field within an @Work bean 
 * in order to tell it which data to use for the dropdown.
 *  (i.e. by overriding the key)
 * 
 * @author Rob Moffat
 *
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiresUserList {

	public static final String USER_LIST_KEY = "userlist";

	public String key() default USER_LIST_KEY;
	
}
