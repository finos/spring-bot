package org.finos.springbot.teams.history;

import static org.finos.springbot.teams.state.TeamsStateStorage.ADDRESSABLE_KEY;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.finos.springbot.teams.content.TeamsAddressable;
import org.finos.springbot.teams.messages.MessageActivityHandler;
import org.finos.springbot.teams.state.TeamsStateStorage;
import org.finos.springbot.teams.state.TeamsStateStorage.Filter;
import org.finos.springbot.workflow.content.Addressable;
import org.finos.springbot.workflow.tags.TagSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This uses Azure's blob storage to store the data-history of chats with the bot. 
 * 
 * @author rob@kite9.com
 *
 */
public class StateStorageBasedTeamsHistory implements TeamsHistory {
	
	private static final String TIMESTAMP_KEY = "timestamp";

	private static final Logger LOG = LoggerFactory.getLogger(MessageActivityHandler.class);
	
	public final TeamsStateStorage tss;

	public StateStorageBasedTeamsHistory(TeamsStateStorage tss) {
		this.tss = tss;
	}

	@Override
	public boolean isSupported(Addressable a) {
		return a instanceof TeamsAddressable;
	}
	
	@Override
 	public <X> Optional<X> getLastFromHistory(Class<X> type, TeamsAddressable address) {
		List<Filter> tags = new ArrayList<>();
		tags.add(new Filter(TagSupport.formatTag(type)));
		tags.add(new Filter(ADDRESSABLE_KEY,address.getKey(), "="));
		return findObjectFromItem(type, tss.retrieve(tags, true), true);

	}

	public <X> Optional<X> getLastFromHistory(Class<X> type, String expectedTag, TeamsAddressable address) {
		List<Filter> tags = new ArrayList<>();
		tags.add(new Filter(expectedTag));
		tags.add(new Filter(ADDRESSABLE_KEY,address.getKey(), "="));
		return findObjectFromItem(type, tss.retrieve(tags, true), true);
	}

	@SuppressWarnings("unchecked")
	public static <X> Optional<X> findObjectFromItem(Class<X> type, Iterable<Map<String, Object>> data, boolean firstOnly) {
		Iterator<Map<String, Object>> it = data.iterator();
		while (it.hasNext()) {
			Map<String, Object> map = it.next();
			for (Object val : map.values()) {
				if (type.isAssignableFrom(val.getClass())) {
					return Optional.of((X) val);
				}
			}
			
			if (firstOnly) {
				LOG.error("Should have found object of type "+type+" inside "+map);
				return Optional.empty();
			}

		}
		
		return Optional.empty();
	}
	
	@SuppressWarnings("unchecked")
	public static <X> List<X> findObjectsFromItems(Class<X> type, Iterable<Map<String, Object>> data) {
		Iterator<Map<String, Object>> it = data.iterator();
		List<X> out = new ArrayList<X>();
		while (it.hasNext()) {
			Map<String, Object> map = it.next();
			for (Object val : map.values()) {
				if (type.isAssignableFrom(val.getClass())) {
					out.add((X) val);
				}
			}
		}
		
		return out;
	}

	protected <X> List<X> getList(Class<X> type, String expectedTag, String directory, long sinceTimestamp) {
		List<Filter> tags = new ArrayList<>();
		tags.add(new Filter(expectedTag));
		tags.add(new Filter(ADDRESSABLE_KEY, directory, "="));
		tags.add(new Filter(TIMESTAMP_KEY, ""+sinceTimestamp , ">="));
		return findObjectsFromItems(type, tss.retrieve(tags, false));
	}

	@Override
	public <X> List<X> getFromHistory(Class<X> type, TeamsAddressable address, Instant since) {
		return getList(type, TagSupport.formatTag(type), address.getKey(), since.getEpochSecond());
	}


	@Override
	public <X> List<X> getFromHistory(Class<X> type, String t, TeamsAddressable address, Instant since) {
		return getList(type, t, address.getKey(), since.getEpochSecond());
	}

	
	

}
