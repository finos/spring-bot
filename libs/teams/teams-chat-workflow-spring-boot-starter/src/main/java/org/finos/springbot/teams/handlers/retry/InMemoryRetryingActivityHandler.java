package org.finos.springbot.teams.handlers.retry;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import org.finos.springbot.teams.content.TeamsAddressable;
import org.finos.springbot.teams.conversations.TeamsConversations;

import com.microsoft.bot.schema.Activity;
import com.microsoft.bot.schema.ResourceResponse;

public class InMemoryRetryingActivityHandler extends AbstractRetryingActivityHandler {

	static final ScheduledExecutorService SCHEDULER = new ScheduledThreadPoolExecutor(10);

	public InMemoryRetryingActivityHandler(TeamsConversations tc) {
		super(tc);
	}

	@Override
	public CompletableFuture<ResourceResponse> handleActivity(Activity activity, TeamsAddressable to) {
		CompletableFuture<ResourceResponse> f = tc.handleActivity(activity, to);
		for (int i = 0; i < teamsRetryCount; i++) {
			f = f.thenApply(CompletableFuture::completedFuture).exceptionally(t -> {
				if (isTooManyRequest(t)) {
					int ra = getRetryAfter((CompletionException) t);
					Executor afterRetryTime = createDelayedExecutor(ra, TimeUnit.SECONDS);
					return CompletableFuture.supplyAsync(() -> null, afterRetryTime)
							.thenCompose(m -> tc.handleActivity(activity, to));
				} else {
					return failed(t);
				}

			}).thenCompose(Function.identity());
		}
		return f;
	}


	private Executor createDelayedExecutor(long delay, TimeUnit unit) {
		return r -> SCHEDULER.schedule(r, delay, unit);
	}

}
