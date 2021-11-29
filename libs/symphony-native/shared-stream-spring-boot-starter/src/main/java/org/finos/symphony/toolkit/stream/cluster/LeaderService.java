package org.finos.symphony.toolkit.stream.cluster;

import java.util.List;

import org.finos.symphony.toolkit.stream.Participant;

public interface LeaderService {

	/**
	 * Tell the leader service that there's a new leader.
	 */
	public void becomeLeader(Participant p);

	/**
	 * Test to find out who the current leader is.
	 */
	public boolean isLeader(Participant p);
	
	/**
	 * Replays the log and finds details of all existing participants.
	 */
	public List<Participant> getRecentParticipants();	
}

