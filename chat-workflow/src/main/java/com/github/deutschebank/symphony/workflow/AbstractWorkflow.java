package com.github.deutschebank.symphony.workflow;

import java.util.List;

import com.github.deutschebank.symphony.workflow.content.Addressable;
import com.github.deutschebank.symphony.workflow.content.Room;
import com.github.deutschebank.symphony.workflow.content.User;

/**
 * Represents workflows which can use the state of the conversation, and knows which rooms it is in.
 * 
 * @author Rob Moffat
 *
 */
public abstract class AbstractWorkflow implements Workflow {
	
	private String namespace;
	private List<User> admins;
	protected List<Room> keyRooms;

	public AbstractWorkflow(String namespace, List<User> admins, List<Room> keyRooms) {
		super();
		this.keyRooms = keyRooms;
		this.admins = admins;
		this.namespace = namespace;
	}

	@Override
	public String getNamespace() {
		return namespace;
	}

	@Override
	public List<Room> getKeyRooms() {
		return keyRooms;
	}

	@Override
	public List<User> getAdministrators() {
		return admins;
	}

	@Override
	public boolean hasMatchingCommand(String name, Addressable r) {
		return getCommands(r).stream()
			.filter(c -> c.getName().equals(name))
			.count() > 0;
	}
	
}
