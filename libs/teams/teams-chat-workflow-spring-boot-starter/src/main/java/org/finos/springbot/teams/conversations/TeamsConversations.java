package org.finos.springbot.teams.conversations;

import java.util.List;

import org.finos.springbot.teams.content.TeamsAddressable;
import org.finos.springbot.teams.content.TeamsChannel;
import org.finos.springbot.teams.content.TeamsChat;
import org.finos.springbot.teams.content.TeamsUser;
import org.finos.springbot.workflow.conversations.PlatformConversations;

import com.microsoft.bot.builder.TurnContext;
import com.microsoft.bot.schema.ChannelAccount;

/**
 * Increases the api-surface area, allowing you to create rooms/users from Teams objects.
 * 
 * @author Rob Moffat
 *
 */
public interface TeamsConversations extends PlatformConversations<TeamsChat, TeamsUser> {

	public TeamsAddressable getTeamsAddressable(TurnContext tc);

	public TeamsAddressable getAddressable(ChannelAccount from);
	
	public TeamsUser getUser(ChannelAccount from);
	
	public List<TeamsChannel> getTeamsChannels(TurnContext tc);
	
	public boolean isChannel(ChannelAccount ca);
	
}
