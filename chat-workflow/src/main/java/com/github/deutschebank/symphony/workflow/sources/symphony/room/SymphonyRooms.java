package com.github.deutschebank.symphony.workflow.sources.symphony.room;

import com.github.deutschebank.symphony.workflow.content.Addressable;
import com.github.deutschebank.symphony.workflow.content.Room;
import com.github.deutschebank.symphony.workflow.content.User;
import com.github.deutschebank.symphony.workflow.room.Rooms;

/**
 * Increases the 
 * @author Rob Moffat
 *
 */
public interface SymphonyRooms extends Rooms {

	public User loadUserById(Long userId);
	
	public Room loadRoomById(String streamId);
	
	public String getStreamFor(Addressable a);
	
	public Long getId(User u);
}
