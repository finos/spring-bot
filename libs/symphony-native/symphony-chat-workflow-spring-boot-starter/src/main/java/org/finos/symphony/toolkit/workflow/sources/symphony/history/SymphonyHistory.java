package org.finos.symphony.toolkit.workflow.sources.symphony.history;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.finos.symphony.toolkit.json.EntityJson;
import org.finos.symphony.toolkit.workflow.content.Tag;
import org.finos.symphony.toolkit.workflow.history.PlatformHistory;
import org.finos.symphony.toolkit.workflow.sources.symphony.content.SymphonyAddressable;

public interface SymphonyHistory extends PlatformHistory<SymphonyAddressable> {

	public <X> Optional<EntityJson> getLastEntityJsonFromHistory(Class<X> type, SymphonyAddressable address);
	
	public <X> Optional<EntityJson> getLastEntityJsonFromHistory(Class<X> type, Tag t, SymphonyAddressable address);
	
	public <X> List<EntityJson> getEntityJsonFromHistory(Class<X> type, SymphonyAddressable address, Instant since);

	public List<EntityJson> getEntityJsonFromHistory(Tag t, SymphonyAddressable address, Instant since);	
	
	public <X> Optional<X> getFromEntityJson(EntityJson ej, Class<X> c);
	
	public <X> List<X> getFromEntityJson(List<EntityJson> ej, Class<X> c);

}
