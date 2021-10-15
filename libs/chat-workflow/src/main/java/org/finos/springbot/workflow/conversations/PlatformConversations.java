package org.finos.springbot.workflow.conversations;

import org.finos.springbot.workflow.content.Chat;
import org.finos.springbot.workflow.content.User;

public interface PlatformConversations<C extends Chat, U extends User> extends Conversations<C, U> {

	public boolean isSupported(Chat r);
	
	public boolean isSupported(User u);
	
}
