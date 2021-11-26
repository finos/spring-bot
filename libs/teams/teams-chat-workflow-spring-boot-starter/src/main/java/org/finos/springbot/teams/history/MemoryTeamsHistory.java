package org.finos.springbot.teams.history;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.finos.springbot.teams.content.TeamsAddressable;
import org.finos.springbot.workflow.content.Addressable;
import org.finos.springbot.workflow.content.Tag;

/**
 * We need one of these for testing, for people who haven't set up blob storage.
 * Suresh to write.
 */
public class MemoryTeamsHistory implements TeamsHistory {

	@Override
	public boolean isSupported(Addressable a) {
		return false;
	}

	@Override
	public <X> Optional<X> getLastFromHistory(Class<X> type, TeamsAddressable address) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <X> Optional<X> getLastFromHistory(Class<X> type, Tag t, TeamsAddressable address) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <X> List<X> getFromHistory(Class<X> type, TeamsAddressable address, Instant since) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <X> List<X> getFromHistory(Class<X> type, Tag t, TeamsAddressable address, Instant since) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void store(TeamsAddressable a, Map<String, Object> data) {
		// TODO Auto-generated method stub
		
	}

}
