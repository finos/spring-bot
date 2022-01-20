package org.finos.springbot.workflow.actions;

import org.finos.springbot.workflow.content.Chat;
import org.finos.springbot.workflow.content.User;

/**
 * Occurs when the membership of a room changes
 * @author rob@kite9.com
 *
 */
public class MemberAction implements Action {

	public enum Type { ADDED, REMOVED }
	
	private final Chat a;
	private final User u;
	private final Type t;
	private final Object d;
	
	public MemberAction(Chat a, User u, Type t, Object d) {
		super();
		this.a = a;
		this.t = t;
		this.u = u;
		this.d = d;
	}

	public Chat getAddressable() {
		return a;
	}

	public Type getType() {
		return t;
	}

	@Override
	public User getUser() {
		return u;
	}

	@Override
	public Object getData() {
		return d;
	}
	
	
	
}
