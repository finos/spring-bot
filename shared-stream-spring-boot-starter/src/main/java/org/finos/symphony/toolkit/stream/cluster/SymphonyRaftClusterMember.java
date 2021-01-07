package org.finos.symphony.toolkit.stream.cluster;

import java.util.function.BooleanSupplier;

import org.finos.symphony.toolkit.stream.Participant;
import org.finos.symphony.toolkit.stream.cluster.transport.Multicaster;
import org.finos.symphony.toolkit.stream.cluster.voting.Decider;
import org.finos.symphony.toolkit.stream.log.SharedLog;

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
	private final BooleanSupplier healthStatus;
	
	public SymphonyRaftClusterMember(Participant self, long timeoutMs, Decider d, Multicaster multicaster, SharedLog sl, BooleanSupplier healthStatus) {
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
		if (healthStatus.getAsBoolean()) {
			super.holdElection();
		}
	}

	
}
