package org.finos.springbot.teams.handlers;

import java.util.concurrent.CompletableFuture;

import org.finos.springbot.teams.content.TeamsAddressable;

import com.microsoft.bot.schema.Activity;
import com.microsoft.bot.schema.ResourceResponse;

public interface ActivityHandler {

	public CompletableFuture<ResourceResponse> handleActivity(Activity activity, TeamsAddressable to);
}
