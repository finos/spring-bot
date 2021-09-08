package org.finos.symphony.toolkit.workflow.sources.symphony.conversations;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.finos.symphony.toolkit.workflow.content.Chat;
import org.finos.symphony.toolkit.workflow.content.User;
import org.finos.symphony.toolkit.workflow.conversations.Conversations;
import org.finos.symphony.toolkit.workflow.sources.symphony.content.SymphonyRoom;
import org.finos.symphony.toolkit.workflow.sources.symphony.content.SymphonyUser;

/**
 * Increases the api-surface area, allowing you to create rooms/users from symphony objects.
 * 
 * @author Rob Moffat
 *
 */
public interface SymphonyConversations extends Conversations {
	
	public static final String ROOM_DESCRIPTION = "room-description";
	public static final String ROOM_PUBLIC = "room-public";

	public SymphonyUser loadUserById(Long userId);
	
	public SymphonyUser loadUserByEmail(String emailAddress);
	
	public SymphonyRoom loadRoomById(String streamId);
	
	public SymphonyRoom loadRoomByName(String name);
	
	public SymphonyRoom ensureChat(Chat r, List<User> users, Map<String, Object> meta);
	
	public static Map<String, Object> simpleMeta(String description, boolean isPublic) {
		Map<String, Object> out = new HashMap<>();
		out.put(ROOM_DESCRIPTION, description);
		out.put(ROOM_PUBLIC, isPublic);
		return out;
	}
}
