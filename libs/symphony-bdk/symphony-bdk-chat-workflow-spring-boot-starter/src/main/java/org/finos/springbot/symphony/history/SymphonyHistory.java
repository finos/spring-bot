package org.finos.springbot.symphony.history;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.finos.springbot.entityjson.EntityJson;
import org.finos.springbot.symphony.content.SymphonyAddressable;
import org.finos.springbot.workflow.content.Tag;
import org.finos.springbot.workflow.history.PlatformHistory;

public interface SymphonyHistory extends PlatformHistory<SymphonyAddressable> {

	public <X> Optional<EntityJson> getLastEntityJsonFromHistory(Class<X> type, SymphonyAddressable address);
	
	public default <X> Optional<EntityJson> getLastEntityJsonFromHistory(Class<X> type, Tag t, SymphonyAddressable address) {
		return getLastEntityJsonFromHistory(type, t.getName(), address);
	}
	
	public <X> Optional<EntityJson> getLastEntityJsonFromHistory(Class<X> type, String t, SymphonyAddressable address);
	
	public <X> List<EntityJson> getEntityJsonFromHistory(Class<X> type, SymphonyAddressable address, Instant since);

	public default List<EntityJson> getEntityJsonFromHistory(Tag t, SymphonyAddressable address, Instant since) {
		return getEntityJsonFromHistory(t.getName(), address, since);
	}
	
	public List<EntityJson> getEntityJsonFromHistory(String t, SymphonyAddressable address, Instant since);	
	
	public <X> Optional<X> getFromEntityJson(EntityJson ej, Class<X> c);
	
	public <X> List<X> getFromEntityJson(List<EntityJson> ej, Class<X> c);

}
