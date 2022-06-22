package org.finos.springbot.teams.conversations;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.finos.springbot.teams.content.TeamsAddressable;
import org.finos.springbot.teams.content.TeamsChannel;
import org.finos.springbot.teams.content.TeamsChat;
import org.finos.springbot.teams.content.TeamsUser;
import org.finos.springbot.workflow.conversations.PlatformConversations;

import com.microsoft.bot.builder.TurnContext;
import com.microsoft.bot.schema.Activity;
import com.microsoft.bot.schema.ChannelAccount;
import com.microsoft.bot.schema.ConversationAccount;
import com.microsoft.bot.schema.ResourceResponse;

/**
 * Increases the api-surface area, allowing you to create rooms/users from Teams objects.
 * 
 * @author Rob Moffat
 *
 */
public interface TeamsConversations extends PlatformConversations<TeamsChat, TeamsUser> {

	public TeamsAddressable getTeamsAddressable(ConversationAccount tc);
	
	public ConversationAccount getConversationAccount(TeamsAddressable ta);

	public TeamsAddressable getAddressable(ChannelAccount from);
	
	public TeamsUser getUser(ChannelAccount from);
	
	public List<TeamsChannel> getTeamsChannels(TurnContext tc);
	
	public boolean isChannel(ChannelAccount ca);
	
	public TeamsUser lookupUser(String userId);
		
	public CompletableFuture<ResourceResponse> handleActivity(Activity a, TeamsAddressable ta);

}
