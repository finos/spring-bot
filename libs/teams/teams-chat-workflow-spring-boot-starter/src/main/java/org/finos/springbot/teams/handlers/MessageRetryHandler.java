package org.finos.springbot.teams.handlers;

import java.util.List;

public interface MessageRetryHandler {		
	
	public void add(MessageRetry t);
	
	public List<MessageRetry> get();
	
}
