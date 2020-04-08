package com.github.deutschebank.symphony.stream.cluster;

import com.github.deutschebank.symphony.stream.Participant;
import com.github.deutschebank.symphony.stream.cluster.transport.Multicaster;
import com.github.deutschebank.symphony.stream.cluster.voting.Decider;
import com.github.deutschebank.symphony.stream.log.SharedLog;

/**
 * Decorates the orginal raft cluter to write a message to the shared log when
 * this member becomes the leader.
 * 
 * @author robmoffat
 *
 */
public class SymphonyRaftClusterMember extends RaftClusterMember {

	private final SharedLog sl;
	
	public SymphonyRaftClusterMember(Participant self, long timeoutMs, Decider d, Multicaster multicaster, SharedLog sl) {
		super(self, timeoutMs, d, multicaster);
		this.sl = sl;
	}

	@Override
	public synchronized void becomeLeader() {
		super.becomeLeader();
		sl.writeLeaderMessage(self);
	}

}
