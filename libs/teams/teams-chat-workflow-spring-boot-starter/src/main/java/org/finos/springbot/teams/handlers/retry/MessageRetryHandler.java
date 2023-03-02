package org.finos.springbot.teams.handlers.retry;

import java.util.concurrent.CompletionException;

import org.apache.commons.lang3.StringUtils;
import org.finos.springbot.workflow.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;

import com.microsoft.bot.connector.rest.ErrorResponseException;

import okhttp3.ResponseBody;

public abstract class MessageRetryHandler implements RetryHandler {

	private static final Logger LOG = LoggerFactory.getLogger(MessageRetryHandler.class);
	
	@Value("${teams.retry.count:3}")
	private long teamsRetryCount;

	public boolean handleException(Response t, int retryCount, Throwable e)  {
		if (e instanceof CompletionException
				&& ((CompletionException) e).getCause() instanceof ErrorResponseException) {
			ErrorResponseException ere = (ErrorResponseException) ((CompletionException) e).getCause();
			retrofit2.Response<ResponseBody> response = ere.response();
			if (response.code() == HttpStatus.TOO_MANY_REQUESTS.value() && retryCount <= teamsRetryCount) {
				String retryAfter = response.headers().get("Retry-After");
				LOG.info("MessageRetryHandler request retryAfter {}", retryAfter);
				
				int retryAfterInt = 1;//initiate to 1 sec
				if(StringUtils.isNumeric(retryAfter)) {
					retryAfterInt = Integer.parseInt(retryAfter);
				}
				
				add(new MessageRetry(t, retryCount, retryAfterInt));
				
				return true;
			}
		}
		
		return false;
	}
	
	protected abstract void add(MessageRetry messageRetry);


}
