package com.github.deutschebank.symphony.workflow.sources.symphony.messages;

import java.util.List;

import com.github.deutschebank.symphony.json.EntityJson;
import com.github.deutschebank.symphony.workflow.Workflow;
import com.github.deutschebank.symphony.workflow.content.Message;
import com.github.deutschebank.symphony.workflow.content.Room;
import com.github.deutschebank.symphony.workflow.content.User;
import com.github.deutschebank.symphony.workflow.sources.symphony.Action;

public class SimpleMessageAction implements Action {
	
	private final Workflow w;
	private final Room r;
	private final User u;
	private final Message words;
	private final EntityJson data;

	public SimpleMessageAction(Workflow w, Room r, User u, Message words, EntityJson ej) {
		super();
		this.w = w;
		this.r = r;
		this.u = u;
		this.words = words;
		this.data = ej;
	}

	@Override
	public Room getRoom() {
		return r;
	}

	@Override
	public User getUser() {
		return u;
	}

	public Message getWords() {
		return words;
	}

	public EntityJson getData() {
		return data;
	}

	public Workflow getWorkflow() {
		return w;
	}
}
