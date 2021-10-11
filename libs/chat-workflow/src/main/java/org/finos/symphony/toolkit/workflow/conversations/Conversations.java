package org.finos.symphony.toolkit.workflow.conversations;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.finos.symphony.toolkit.workflow.content.Addressable;
import org.finos.symphony.toolkit.workflow.content.Chat;
import org.finos.symphony.toolkit.workflow.content.User;

public interface Conversations<C extends Chat, U extends User> {

	
	/**
	 * Returns all the conversations that the bot is a member of.
	 */
	public Set<Addressable> getAllAddressables();

	/**
	 * Returns the subset of all conversations that are chats.
	 */
	public Set<C> getAllChats();
	
	public C getExistingChat(String name);

	public C ensureChat(C r, List<U> users, Map<String, Object> meta);
	
	public List<U> getChatMembers(C r);
	
	public List<U> getChatAdmins(C r);
	
}
