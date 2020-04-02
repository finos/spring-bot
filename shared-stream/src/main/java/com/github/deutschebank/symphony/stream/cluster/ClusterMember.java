package com.github.deutschebank.symphony.stream.cluster;

import com.github.deutschebank.symphony.stream.Participant;
import com.github.deutschebank.symphony.stream.cluster.messages.SuppressionMessage;
import com.github.deutschebank.symphony.stream.cluster.messages.VoteRequest;
import com.github.deutschebank.symphony.stream.cluster.messages.VoteResponse;

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
	
	public void receivePing(SuppressionMessage sm);
	
	public VoteResponse receiveVoteRequest(VoteRequest vr);
	
	//public void receiveEvent(ID id);
	
	public void becomeLeader();
	
	public Participant getSelfDetails();
	
	public State getState();
}
