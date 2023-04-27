package org.finos.springbot.teams.handlers.retry;


import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import org.apache.commons.lang3.StringUtils;
import org.finos.springbot.teams.conversations.TeamsConversations;
import org.finos.springbot.teams.handlers.ActivityHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;

import com.microsoft.bot.connector.rest.ErrorResponseException;

import okhttp3.ResponseBody;

public abstract class AbstractRetryingActivityHandler implements ActivityHandler {

	protected static final Logger LOG = LoggerFactory.getLogger(AbstractRetryingActivityHandler.class);

	@Value("${teams.retry.count:3}")
	protected long teamsRetryCount = 3;

	protected TeamsConversations tc;
	
	public AbstractRetryingActivityHandler(TeamsConversations tc) {
		this.tc = tc;
	}

	protected boolean isTooManyRequest(Throwable e) {
		if (e instanceof CompletionException
				&& ((CompletionException) e).getCause() instanceof ErrorResponseException) {
			ErrorResponseException ere = (ErrorResponseException) ((CompletionException) e).getCause();
			retrofit2.Response<ResponseBody> response = ere.response();
			return (response.code() == HttpStatus.TOO_MANY_REQUESTS.value());
		} else {
			return false;
		}
	}

	protected int getRetryAfter(CompletionException e) {
		ErrorResponseException ere = (ErrorResponseException) ((CompletionException) e).getCause();
		retrofit2.Response<ResponseBody> response = ere.response();
		String retryAfter = response.headers().get("Retry-After");

		int retryAfterInt = 1;// initiate to 1 sec
		if (StringUtils.isNumeric(retryAfter)) {
			retryAfterInt = Integer.parseInt(retryAfter);
		}

		return retryAfterInt;
	}

	protected static <R> CompletableFuture<R> failed(Throwable error) {
	    CompletableFuture<R> future = new CompletableFuture<>();
	    future.completeExceptionally(error);
	    return future;
	}

	
}