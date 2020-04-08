package com.github.deutschebank.symphony.stream.cluster;

import java.util.function.Supplier;

import com.github.deutschebank.symphony.stream.Participant;
import com.github.deutschebank.symphony.stream.cluster.transport.Multicaster;
import com.github.deutschebank.symphony.stream.cluster.voting.Decider;
import com.github.deutschebank.symphony.stream.log.SharedLog;

/**
 * Decorates the orginal raft cluter to write a message to the shared log when
 * this member becomes the leader.
 * 
 * Additionally, only allows the member to become a leader IF they are healthy (according to 
 * the supplied health status). 
 * 
 * @author robmoffat
 *
 */
public class SymphonyRaftClusterMember extends RaftClusterMember {

	private final SharedLog sl;
	private final Supplier<Boolean> healthStatus;
	
	public SymphonyRaftClusterMember(Participant self, long timeoutMs, Decider d, Multicaster multicaster, SharedLog sl, Supplier<Boolean> healthStatus) {
		super(self, timeoutMs, d, multicaster);
		this.sl = sl;
		this.healthStatus = healthStatus;
	}

	@Override
	public synchronized void becomeLeader() {
		super.becomeLeader();
		sl.writeLeaderMessage(self);
	}

	@Override
	public synchronized void holdElection() {
		if (healthStatus.get()) {
			super.holdElection();
		}
	}

	
}
