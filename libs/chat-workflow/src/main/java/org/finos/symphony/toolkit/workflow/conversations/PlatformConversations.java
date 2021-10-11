package org.finos.symphony.toolkit.workflow.conversations;

import org.finos.symphony.toolkit.workflow.content.Chat;
import org.finos.symphony.toolkit.workflow.content.User;

public interface PlatformConversations<C extends Chat, U extends User> extends Conversations<C, U> {

	public boolean isSupported(Chat r);
	
	public boolean isSupported(User u);
	
}
