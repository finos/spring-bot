package org.finos.springbot.teams.handlers;

import java.util.Optional;

public interface MessageRetryHandler {		
	
	public void add(MessageRetry t);
	
	public Optional<MessageRetry> get();
	
}
