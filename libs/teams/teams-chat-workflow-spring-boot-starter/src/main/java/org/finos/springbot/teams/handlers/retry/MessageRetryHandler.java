package org.finos.springbot.teams.handlers.retry;

import java.util.Optional;

import org.finos.springbot.workflow.response.Response;

public interface MessageRetryHandler {		
	
	public boolean handleException(Response t, int retryCount, Throwable e);
	
	public Optional<MessageRetry> get();
	
}
