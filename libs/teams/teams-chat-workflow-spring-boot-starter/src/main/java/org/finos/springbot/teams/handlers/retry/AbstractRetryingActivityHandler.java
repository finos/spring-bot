package org.finos.springbot.teams.handlers.retry;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import org.apache.commons.lang3.StringUtils;
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

public abstract class AbstractRetryingActivityHandler implements ActivityHandler {

	protected static final Logger LOG = LoggerFactory.getLogger(AbstractRetryingActivityHandler.class);

	private TeamsConversations tc;

	@Value("${teams.retry.count:3}")
	private long teamsRetryCount;

	public AbstractRetryingActivityHandler(TeamsConversations tc) {
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

	public long getRetryAfter(Exception e) {
		ErrorResponseException ere = (ErrorResponseException) ((CompletionException) e).getCause();
		retrofit2.Response<ResponseBody> response = ere.response();
		String retryAfter = response.headers().get("Retry-After");

		LOG.info("MessageRetryHandler request retryAfter {}", retryAfter);
		long retryAfterInt = 1;// initiate to 1 sec
		if (StringUtils.isNumeric(retryAfter)) {
			retryAfterInt = Long.parseLong(retryAfter);
		}

		return retryAfterInt;
	}

	@Override
	public CompletableFuture<ResourceResponse> handleActivity(Activity activity, TeamsAddressable to) {
		return handleActivity(activity, to, 0);
	}

	public CompletableFuture<ResourceResponse> handleActivity(Activity activity, TeamsAddressable to, Integer retryCount) {
		return tc.handleActivity(activity, to).handle((rr, ex) -> {
			if (ex != null) {
				handleException(activity, to, retryCount, ex);
			}
			return rr;
		});
	}

	public Boolean handleException(Activity activity, TeamsAddressable to, int retryCount, Throwable e) {
		if (isTooManyRequest(e)) {
			long retryAfter = getRetryAfter((CompletionException) e);
			retryCount++;
			if (retryCount <= teamsRetryCount) {
				LOG.info("MessageRetryHandler request retryAfter {}", retryAfter);
				LocalDateTime time = LocalDateTime.now().plusSeconds(retryAfter);
				add(new MessageRetry(activity, to, retryCount, retryAfter, time, e));
				return true;
			}
		}
		return false;
	}
	
	public void retryMessage() throws Throwable {
		int messageCount = 0;
		Optional<MessageRetry> opt;
		while ((opt = get()).isPresent()) {
			messageCount++;
			MessageRetry msg = opt.get();
			this.handleActivity(msg.getActivity(), msg.getTo(), msg.getRetryCount());
		}

		LOG.info("Retry message queue {}", messageCount == 0 ? "is empty" : "has messages, count: " + messageCount);
	}

	
	protected abstract void add(MessageRetry retry) ;
	
	protected abstract Optional<MessageRetry> get() ;
	
	class MessageRetry {

		private Activity activity;
		private TeamsAddressable to;
		private int retryCount;
		private long retryAfter;
		private LocalDateTime retryTime;
		private Throwable e;
		
		
		public MessageRetry(Activity activity, TeamsAddressable to, int retryCount, long retryAfter,
				LocalDateTime retryTime, Throwable e) {
			super();
			this.activity = activity;
			this.to = to;
			this.retryCount = retryCount;
			this.retryAfter = retryAfter;
			this.retryTime = retryTime;
			this.e= e; 
		}

		public Activity getActivity() {
			return activity;
		}

		public void setActivity(Activity activity) {
			this.activity = activity;
		}

		public TeamsAddressable getTo() {
			return to;
		}

		public void setTo(TeamsAddressable to) {
			this.to = to;
		}

		public int getRetryCount() {
			return retryCount;
		}

		public void setRetryCount(int retryCount) {
			this.retryCount = retryCount;
		}

		public long getRetryAfter() {
			return retryAfter;
		}

		public void setRetryAfter(long retryAfter) {
			this.retryAfter = retryAfter;
		}

		public LocalDateTime getRetryTime() {
			return retryTime;
		}

		public void setRetryTime(LocalDateTime retryTime) {
			this.retryTime = retryTime;
		}

		public Throwable getE() {
			return e;
		}

		public void setE(Throwable e) {
			this.e = e;
		}
		
	}
}
