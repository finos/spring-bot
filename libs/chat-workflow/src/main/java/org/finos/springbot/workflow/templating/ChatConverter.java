package org.finos.springbot.workflow.templating;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

import org.finos.springbot.workflow.annotations.RequiresChatList;

/**
 * Provides a simple drop-down for selecting a chat that the bot is a member of.
 */
public class ChatConverter<X> extends AbstractClassConverter<X> {
	
	public ChatConverter(int priority, Rendering<X> r, Class<?>... forClass) {
		super(priority, r, forClass);
	}

	@Override
	public X apply(Field ctx, Type t, boolean editMode, Variable v) {
		return r.renderDropdown(v, "key", getLocation(ctx), "key", "name", editMode);
	}
	
	protected String getLocation(Field ctx) {
		RequiresChatList rul = ctx.getAnnotation(RequiresChatList.class);
		return rul == null ? RequiresChatList.CHAT_LIST_KEY+".contents" : rul.key(); 
	}
}
