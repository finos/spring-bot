package org.finos.symphony.toolkit.workflow.history;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.finos.symphony.toolkit.workflow.content.Addressable;
import org.finos.symphony.toolkit.workflow.content.Tag;

public interface History<A extends Addressable> {
	
	public <X> Optional<X> getLastFromHistory(Class<X> type, A address);
	
	public <X> Optional<X> getLastFromHistory(Class<X> type, Tag t, A address);
	
	public <X> List<X> getFromHistory(Class<X> type, A address, Instant since);

	public <X> List<X> getFromHistory(Class<X> type, Tag t, A address, Instant since);

}