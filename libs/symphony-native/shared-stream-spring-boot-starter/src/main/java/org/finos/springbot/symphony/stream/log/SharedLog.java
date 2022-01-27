package org.finos.springbot.symphony.stream.log;

import java.util.Optional;

import org.finos.springbot.symphony.stream.Participant;
import org.finos.springbot.symphony.stream.cluster.LeaderService;

/**
 * The shared log is used for cluster coordination, and is expected to be implemented as a Symphony Room.
 */
public interface SharedLog extends LogMessageHandler, LeaderService {

	/**
	 * Tells all other members (via the shared log) that p is taking over leadership duties of the cluster
	 */
	public void writeLeaderMessage(Participant p);

	/**
	 * Tells all other members (via the shared log) that p is available to be considered for leadership duties.
	 * @param p
	 */
	public void writeParticipantMessage(Participant p);
	
	/**
	 * Returns the last recorded leader from the log.  Should be cached as this is an expensive call.
	 */
	public Optional<Participant> getLastRecordedLeader(Participant me);
}
