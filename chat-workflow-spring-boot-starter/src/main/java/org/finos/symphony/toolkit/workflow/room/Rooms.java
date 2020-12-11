package org.finos.symphony.toolkit.workflow.room;

import java.util.List;
import java.util.Set;

import org.finos.symphony.toolkit.workflow.content.Room;
import org.finos.symphony.toolkit.workflow.content.User;

public interface Rooms {

	/**
	 * Returns all the rooms that the bot is a member of.
	 */
	public Set<Room> getAllRooms();
	
	public Room ensureRoom(Room r);
	
	public List<User> getRoomMembers(Room r);
	
	public List<User> getRoomAdmins(Room r);

}
