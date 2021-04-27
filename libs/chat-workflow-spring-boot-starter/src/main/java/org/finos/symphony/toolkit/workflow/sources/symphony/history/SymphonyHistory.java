package org.finos.symphony.toolkit.workflow.sources.symphony.history;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.finos.symphony.toolkit.json.EntityJson;
import org.finos.symphony.toolkit.workflow.content.Addressable;
import org.finos.symphony.toolkit.workflow.content.Tag;
import org.finos.symphony.toolkit.workflow.history.History;

public interface SymphonyHistory extends History {

	public <X> Optional<EntityJson> getLastEntityJsonFromHistory(Class<X> type, Addressable address);
	
	public <X> Optional<EntityJson> getLastEntityJsonFromHistory(Class<X> type, Tag t, Addressable address);
	
	public <X> List<EntityJson> getEntityJsonFromHistory(Class<X> type, Addressable address, Instant since);

	public List<EntityJson> getEntityJsonFromHistory(Tag t, Addressable address, Instant since);	
	
	public <X> Optional<X> getFromEntityJson(EntityJson ej, Class<X> c);
	
	public <X> List<X> getFromEntityJson(List<EntityJson> ej, Class<X> c);

}
