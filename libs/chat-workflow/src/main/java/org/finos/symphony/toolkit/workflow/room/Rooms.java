package org.finos.symphony.toolkit.workflow.room;

import java.util.List;
import java.util.Set;

import org.finos.symphony.toolkit.workflow.content.Chat;
import org.finos.symphony.toolkit.workflow.content.User;

public interface Rooms {

	/**
	 * Returns all the rooms that the bot is a member of.
	 */
	public Set<Chat> getAllRooms();
	
	public Chat ensureRoom(Chat r);
	
	public List<User> getRoomMembers(Chat r);
	
	public List<User> getRoomAdmins(Chat r);

}
