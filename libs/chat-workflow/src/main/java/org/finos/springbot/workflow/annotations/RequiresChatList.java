package org.finos.springbot.workflow.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Add this annotation to an @Work-annotated class in order to populate the 
 * response data with a list of chats that the bot is aware of.
 * 
 * This is used for populating a dropdown for chat fields.
 * 
 * @author Rob Moffat
 *
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiresChatList {

	public static final String CHAT_LIST_KEY = "chatlist";

	public String key() default CHAT_LIST_KEY;

}
