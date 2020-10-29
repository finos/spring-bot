package org.finos.symphony.toolkit.stream.cluster;

import org.finos.symphony.toolkit.stream.Participant;
import org.finos.symphony.toolkit.stream.cluster.messages.ClusterMessage;

/**
 * A cluster implements a raft-style algorithm, in which an election is held.  A
 * leader is elected with (1/2n+1) of participants.
 * 
 * @author robmoffat
 *
 */
public interface ClusterMember {
	
	public static enum State { LEADER, SUPRESSED, PROPOSING_ELECTION, STOPPED }

	public void startup();
	
	public void shutdown();
	
	public ClusterMessage receiveMessage(ClusterMessage cm);
	
	//public void receiveEvent(ID id);
	
	public void becomeLeader();
	
	public Participant getSelfDetails();
	
	public State getState();
}
