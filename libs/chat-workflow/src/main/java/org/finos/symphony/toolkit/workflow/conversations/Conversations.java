package org.finos.symphony.toolkit.workflow.conversations;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.finos.symphony.toolkit.workflow.content.Addressable;
import org.finos.symphony.toolkit.workflow.content.Chat;
import org.finos.symphony.toolkit.workflow.content.User;

public interface Conversations {

	
	/**
	 * Returns all the conversations that the bot is a member of.
	 */
	public Set<Addressable> getAllConversations();

	/**
	 * Returns the subset of all conversations that are chats.
	 */
	public Set<Chat> getAllChats();
	
	public Chat getExistingChat(String name);

	public Chat ensureChat(Chat r, List<User> users, Map<String, Object> meta);
	
	public List<User> getChatMembers(Chat r);
	
	public List<User> getChatAdmins(Chat r);

}
