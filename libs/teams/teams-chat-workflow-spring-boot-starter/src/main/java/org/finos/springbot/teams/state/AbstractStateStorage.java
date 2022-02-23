package org.finos.springbot.teams.state;

import java.util.UUID;

public abstract class AbstractStateStorage implements TeamsStateStorage {

	/**
	 * The blob ID should be alphabetically ordered so that newer blobs are 
	 * earlier in the alphabet.  That's quite tricky, so subtracting from the long of the current time
	 */
	@Override
	public String createStorageId() {
		long ts = Long.MAX_VALUE;
		ts = ts - System.currentTimeMillis();
		
		return ""+ts+"-"+UUID.randomUUID().toString();
	}
}
