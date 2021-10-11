package org.finos.springbot.sources.teams.conversations;

import org.finos.symphony.toolkit.workflow.content.Addressable;
import org.finos.symphony.toolkit.workflow.conversations.Conversations;

import com.microsoft.bot.schema.teams.TeamsChannelData;

/**
 * Increases the api-surface area, allowing you to create rooms/users from Teams objects.
 * 
 * @author Rob Moffat
 *
 */
public interface TeamsConversations extends Conversations {

	Addressable getTeamsChat(TeamsChannelData tcd);
	
}
