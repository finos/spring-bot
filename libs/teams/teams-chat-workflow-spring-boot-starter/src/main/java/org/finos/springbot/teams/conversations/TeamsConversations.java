package org.finos.springbot.teams.conversations;

import org.finos.springbot.teams.content.TeamsAddressable;
import org.finos.springbot.teams.content.TeamsChat;
import org.finos.springbot.teams.content.TeamsUser;
import org.finos.springbot.workflow.content.User;
import org.finos.springbot.workflow.conversations.PlatformConversations;

import com.microsoft.bot.builder.TurnContext;
import com.microsoft.bot.schema.Activity;
import com.microsoft.bot.schema.ChannelAccount;

/**
 * Increases the api-surface area, allowing you to create rooms/users from Teams objects.
 * 
 * @author Rob Moffat
 *
 */
public interface TeamsConversations extends PlatformConversations<TeamsChat, TeamsUser> {

	TeamsAddressable getTeamsChat(TurnContext tc);

	public User getUser(ChannelAccount from);
	
}
