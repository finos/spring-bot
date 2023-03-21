package org.finos.springbot.teams.handlers.retry;

import java.util.Optional;

import org.finos.springbot.workflow.response.Response;

public class NoOpRetryHandler implements MessageRetryHandler {

	@Override
	public Optional<MessageRetry> get() {
		return Optional.empty();
	}

	@Override
	public boolean handleException(Response t, int retryCount, Throwable e) {

		return false;
	}

	@Override
	public void add(MessageRetry messageRetry) {
		//Do-Nothing
	}

	@Override
	public void clearAll() {
		//Do-Nothing
	}

}
