package org.finos.springbot.sources.teams.conversations;

import org.finos.springbot.sources.teams.content.TeamsChat;
import org.finos.springbot.sources.teams.content.TeamsUser;
import org.finos.symphony.toolkit.workflow.content.Addressable;
import org.finos.symphony.toolkit.workflow.conversations.PlatformConversations;

import com.microsoft.bot.schema.teams.TeamsChannelData;

/**
 * Increases the api-surface area, allowing you to create rooms/users from Teams objects.
 * 
 * @author Rob Moffat
 *
 */
public interface TeamsConversations extends PlatformConversations<TeamsChat, TeamsUser> {

	Addressable getTeamsChat(TeamsChannelData tcd);
	
}
