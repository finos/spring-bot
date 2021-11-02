package org.finos.springbot.workflow.templating;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

import org.finos.springbot.workflow.annotations.RequiresUserList;

/**
 * Provides a simple drop-down for selecting a user within the room.
 */
public class UserConverter<X> extends AbstractClassConverter<X> {
	
	public UserConverter(int priority, Rendering<X> r, Class<?>... forClass) {
		super(priority, r, forClass);
	}

	@Override
	public X apply(Field ctx, Type t, boolean editMode, Variable v) {
		return r.renderDropdown(v, ".key", getLocation(ctx), ".key", ".name", editMode);
	}
	
	protected String getLocation(Field ctx) {
		if (ctx == null) {
			return RequiresUserList.USER_LIST_KEY+".contents";
		} else {
			RequiresUserList rul = ctx.getAnnotation(RequiresUserList.class);
			return rul == null ? RequiresUserList.USER_LIST_KEY+ ".contents" : rul.key(); 
		}
	}
}
