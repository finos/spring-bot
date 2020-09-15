package com.github.deutschebank.symphony.workflow.room;

import java.util.Set;

import com.github.deutschebank.symphony.workflow.content.Room;

public interface Rooms {

	/**
	 * Returns all the rooms that the bot is a member of.
	 */
	public Set<Room> getAllRooms();
	
	public Room ensureRoom(Room r);

}
