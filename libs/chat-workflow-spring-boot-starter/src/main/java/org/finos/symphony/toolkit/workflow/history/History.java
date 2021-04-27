package org.finos.symphony.toolkit.workflow.history;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.finos.symphony.toolkit.workflow.content.Addressable;
import org.finos.symphony.toolkit.workflow.content.Tag;

public interface History {
	
	public <X> Optional<X> getLastFromHistory(Class<X> type, Addressable address);
	
	public <X> Optional<X> getLastFromHistory(Class<X> type, Tag t, Addressable address);
	
	public <X> List<X> getFromHistory(Class<X> type, Addressable address, Instant since);

	public <X> List<X> getFromHistory(Class<X> type, Tag t, Addressable address, Instant since);

}