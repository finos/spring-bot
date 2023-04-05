package org.finos.springbot.teams.handlers;

import java.util.concurrent.CompletableFuture;

import org.finos.springbot.teams.content.TeamsAddressable;
import org.finos.springbot.teams.conversations.TeamsConversations;

import com.microsoft.bot.schema.Activity;
import com.microsoft.bot.schema.ResourceResponse;

public class SimpleActivityHandler implements ActivityHandler {

	private TeamsConversations tc;

	public SimpleActivityHandler(TeamsConversations tc) {
		this.tc = tc;
	}

	@Override
	public CompletableFuture<ResourceResponse> handleActivity(Activity activity, TeamsAddressable to) {
		return tc.handleActivity(activity, to);
	}

}
