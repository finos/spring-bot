package org.finos.symphony.toolkit.stream.log;

import java.util.List;
import java.util.Optional;

import org.finos.symphony.toolkit.stream.Participant;

/**
 * The shared log is used for cluster coordination, and is expected to be implemented as a Symphony Room.
 */
public interface SharedLog {

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
	 * Replays the log and finds details of all existing participants.
	 * @return
	 */
	public List<Participant> getRegisteredParticipants(Participant me);
	
	/**
	 * Returns the last recorded leader from the log.  Should be cached as this is an expensive call.
	 */
	public Optional<Participant> getLeader(Participant me);
}
