package org.finos.springbot.teams.handlers.retry;

import java.util.Optional;

import org.finos.springbot.workflow.response.Response;

public class NoOpRetryHandler implements RetryHandler {

	@Override
	public Optional<MessageRetry> get() {
		return Optional.empty();
	}

	@Override
	public boolean handleException(Response t, int retryCount, Throwable e) {

		return false;
	}

}
