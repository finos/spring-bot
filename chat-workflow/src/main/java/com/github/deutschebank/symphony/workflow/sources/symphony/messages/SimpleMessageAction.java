package com.github.deutschebank.symphony.workflow.sources.symphony.messages;

import com.github.deutschebank.symphony.json.EntityJson;
import com.github.deutschebank.symphony.workflow.Action;
import com.github.deutschebank.symphony.workflow.Workflow;
import com.github.deutschebank.symphony.workflow.content.Addressable;
import com.github.deutschebank.symphony.workflow.content.Message;
import com.github.deutschebank.symphony.workflow.content.User;

public class SimpleMessageAction implements Action {
	
	private final Workflow w;
	private final Addressable a;
	private final User u;
	private final Message words;
	private final EntityJson data;

	public SimpleMessageAction(Workflow w, Addressable a, User u, Message words, EntityJson ej) {
		super();
		this.w = w;
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

	public Workflow getWorkflow() {
		return w;
	}
}
