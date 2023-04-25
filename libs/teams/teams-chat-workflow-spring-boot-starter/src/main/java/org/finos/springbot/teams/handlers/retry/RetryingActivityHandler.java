package org.finos.springbot.teams.handlers.retry;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.concurrent.ConcurrentUtils;
import org.finos.springbot.teams.content.TeamsAddressable;
import org.finos.springbot.teams.conversations.TeamsConversations;
import org.finos.springbot.teams.handlers.ActivityHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;

import com.microsoft.bot.connector.rest.ErrorResponseException;
import com.microsoft.bot.schema.Activity;
import com.microsoft.bot.schema.ResourceResponse;

import okhttp3.ResponseBody;

public class RetryingActivityHandler implements ActivityHandler {

	protected static final Logger LOG = LoggerFactory.getLogger(RetryingActivityHandler.class);

	private TeamsConversations tc;

	@Value("${teams.retry.count:3}")
	private long teamsRetryCount = 3;

	public RetryingActivityHandler(TeamsConversations tc) {
		this.tc = tc;
	}

	public boolean isTooManyRequest(Throwable e) {
		if (e instanceof CompletionException
				&& ((CompletionException) e).getCause() instanceof ErrorResponseException) {
			ErrorResponseException ere = (ErrorResponseException) ((CompletionException) e).getCause();
			retrofit2.Response<ResponseBody> response = ere.response();
			return (response.code() == HttpStatus.TOO_MANY_REQUESTS.value());
		} else {
			return false;
		}
	}

	public long getRetryAfter(CompletionException e) {
		ErrorResponseException ere = (ErrorResponseException) ((CompletionException) e).getCause();
		retrofit2.Response<ResponseBody> response = ere.response();
		String retryAfter = response.headers().get("Retry-After");

		long retryAfterInt = 1;// initiate to 1 sec
		if (StringUtils.isNumeric(retryAfter)) {
			retryAfterInt = Long.parseLong(retryAfter);
		}

		return retryAfterInt;
	}

	@Override
	public CompletableFuture<ResourceResponse> handleActivity(Activity activity, TeamsAddressable to) {
		CompletableFuture<ResourceResponse> f = tc.handleActivity(activity, to);
		for (int i = 0; i < teamsRetryCount; i++) {
			f = f.thenApply(CompletableFuture::completedFuture).exceptionally(t -> {
				if (isTooManyRequest(t)) {
					long ra = getRetryAfter((CompletionException) t);
					
					Executor afterRetryTime = createDelayedExecutor(ra, TimeUnit.MILLISECONDS);
					
					return CompletableFuture.supplyAsync(() -> null, afterRetryTime)
						.thenCompose(m -> tc.handleActivity(activity, to));
					
				} else {
					return failed(t);
				}

			}).thenCompose(Function.identity());
		}
		return f;
	}
	
	public static <R> CompletableFuture<R> failed(Throwable error) {
	    CompletableFuture<R> future = new CompletableFuture<>();
	    future.completeExceptionally(error);
	    return future;
	}

	static final ScheduledExecutorService SCHEDULER = new ScheduledThreadPoolExecutor(5);
	
	private Executor createDelayedExecutor(long delay, TimeUnit unit) {
		return r -> SCHEDULER.schedule(r, delay, unit);
	}

}