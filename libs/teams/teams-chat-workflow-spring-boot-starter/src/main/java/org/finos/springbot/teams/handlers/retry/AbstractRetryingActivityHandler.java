package org.finos.springbot.teams.handlers.retry;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import org.apache.commons.lang3.StringUtils;
import org.finos.springbot.teams.TeamsException;
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

	private static final int INIT_RETRY_COUNT = 0;

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
		return handleActivity(activity, to, INIT_RETRY_COUNT);
	}

	public CompletableFuture<ResourceResponse> handleActivity(Activity activity, TeamsAddressable to,
			Integer retryCount) {
		return tc.handleActivity(activity, to).handle((rr, ex) -> {
			if (ex != null) {
				Boolean success = handleToManyRequestException(activity, to, retryCount, ex);
				if (!success) {
					throw new TeamsException("Couldn't handle response ", ex);
				} else {
					return null; 
				}
			} else {
				return rr;
			}

		});
	}
	
	private CompletableFuture<ResourceResponse> executeMycustomActionHere(Activity activity, TeamsAddressable to) {
		return tc.handleActivity(activity, to);
	}
	
	public CompletableFuture<ResourceResponse> executeActionAsync(Activity activity, TeamsAddressable to) {
	    CompletableFuture<ResourceResponse> f=executeMycustomActionHere(activity, to);
	    for(int i=0; i<teamsRetryCount; i++) {
	        f=f.thenApply(CompletableFuture::completedFuture)
	           .exceptionally(t -> executeMycustomActionHere())
	           .thenCompose(Function.identity());
	    }
	    return f;
	}å

	public Boolean handleToManyRequestException(Activity activity, TeamsAddressable to, int retryCount, Throwable e) {
		if (isTooManyRequest(e)) {
			long retryAfter = getRetryAfter((CompletionException) e);
			retryCount++;
			if (retryCount <= teamsRetryCount) {
				LOG.info("AbstractRetryingActivityHandler request retryAfter {}", retryAfter);
				LocalDateTime retryAfterTime = LocalDateTime.now().plusSeconds(retryAfter);
				add(new MessageRetry(activity, to, retryCount, retryAfterTime));
				return true;
			}
		}
		return false;
	}

	public void retryMessage() {
		int messageCount = 0;
		Optional<MessageRetry> opt;
		while ((opt = get()).isPresent()) {
			messageCount++;
			MessageRetry msg = opt.get();
			this.handleActivity(msg.getActivity(), msg.getAddressable(), msg.getRetryCount());
		}

		LOG.info("Retry message queue {}", messageCount == 0 ? "is empty" : "has messages, count: " + messageCount);
	}

	protected abstract void add(MessageRetry retry);

	protected abstract Optional<MessageRetry> get();

	class MessageRetry {

		private Activity activity;
		private TeamsAddressable addressable;
		private int retryCount;
		private LocalDateTime retryAfterTime;

		public MessageRetry(Activity activity, TeamsAddressable addressable, int retryCount,
				LocalDateTime retryAfterTime) {
			super();
			this.activity = activity;
			this.addressable = addressable;
			this.retryCount = retryCount;
			this.retryAfterTime = retryAfterTime;
		}

		public Activity getActivity() {
			return activity;
		}

		public void setActivity(Activity activity) {
			this.activity = activity;
		}

		public TeamsAddressable getAddressable() {
			return addressable;
		}

		public void setAddressable(TeamsAddressable addressable) {
			this.addressable = addressable;
		}

		public int getRetryCount() {
			return retryCount;
		}

		public void setRetryCount(int retryCount) {
			this.retryCount = retryCount;
		}

		public LocalDateTime getRetryAfterTime() {
			return retryAfterTime;
		}

		public void setRetryAfterTime(LocalDateTime retryAfterTime) {
			this.retryAfterTime = retryAfterTime;
		}

	}
}
