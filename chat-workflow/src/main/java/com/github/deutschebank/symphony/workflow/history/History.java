package com.github.deutschebank.symphony.workflow.history;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import com.github.deutschebank.symphony.workflow.content.Addressable;

public interface History {
	
	public <X> Optional<X> getLastFromHistory(Class<X> type, Addressable address);
	
	public <X> List<X> getFromHistory(Class<X> type, Addressable address, Instant since);
		
}