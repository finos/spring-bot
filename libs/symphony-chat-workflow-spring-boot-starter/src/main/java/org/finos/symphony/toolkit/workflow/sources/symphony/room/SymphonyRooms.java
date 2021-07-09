package org.finos.symphony.toolkit.workflow.sources.symphony.room;

import org.finos.symphony.toolkit.workflow.room.Rooms;
import org.finos.symphony.toolkit.workflow.sources.symphony.content.SymphonyRoom;
import org.finos.symphony.toolkit.workflow.sources.symphony.content.SymphonyUser;

/**
 * Increases the api-surface area, allowing you to create rooms/users from symphony objects.
 * 
 * @author Rob Moffat
 *
 */
public interface SymphonyRooms extends Rooms {

	public SymphonyUser loadUserById(Long userId);
	
	public SymphonyRoom loadRoomById(String streamId);
	
}
