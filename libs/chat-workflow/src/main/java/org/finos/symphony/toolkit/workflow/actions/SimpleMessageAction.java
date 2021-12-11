package org.finos.symphony.toolkit.workflow.actions;

import org.finos.symphony.toolkit.workflow.content.Addressable;
import org.finos.symphony.toolkit.workflow.content.Message;
import org.finos.symphony.toolkit.workflow.content.User;

public class SimpleMessageAction implements Action {
	
	private final Addressable a;
	private final User u;
	private final Message words;
	private final Object data;

	public SimpleMessageAction(Addressable a, User u, Message words, Object ej) {
		super();
		this.a = a;
		this.u = u;
		this.words = words;
		this.data = ej;
	}

	@Override
	public Addressable getAddressable() {
		return a;
	}

	@Override
	public User getUser() {
		return u;
	}

	public Message getMessage() {
		return words;
	}

	@Override
	public Object getData() {
		return data;
	}

}
