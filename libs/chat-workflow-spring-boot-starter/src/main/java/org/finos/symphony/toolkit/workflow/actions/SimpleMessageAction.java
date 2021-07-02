package org.finos.symphony.toolkit.workflow.actions;

import org.finos.symphony.toolkit.json.EntityJson;
import org.finos.symphony.toolkit.workflow.content.Addressable;
import org.finos.symphony.toolkit.workflow.content.Message;
import org.finos.symphony.toolkit.workflow.content.User;

public class SimpleMessageAction implements Action {
	
	private final Addressable a;
	private final User u;
	private final Message words;
	private final EntityJson data;

	public SimpleMessageAction(Addressable a, User u, Message words, EntityJson ej) {
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

	public Message getWords() {
		return words;
	}

	@Override
	public EntityJson getData() {
		return data;
	}

}
