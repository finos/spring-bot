package org.finos.symphony.toolkit.stream.fixture;

import org.finos.symphony.toolkit.stream.Participant;
import org.finos.symphony.toolkit.stream.cluster.RaftClusterMember;
import org.finos.symphony.toolkit.stream.cluster.transport.Multicaster;
import org.finos.symphony.toolkit.stream.cluster.voting.Decider;

public class TestClusterMember extends RaftClusterMember {
	
	
	
	public TestClusterMember(Participant self, long timeoutMs, Decider d, Multicaster multicaster) {
		super("test", self, timeoutMs, d, multicaster);
	}


	public TestClusterMember(Participant self, long timeoutMs, TestNetwork n, Decider d) {
		super("test", self,timeoutMs, d, n);
		n.register(self, this);
		// System.out.println(self+" with timeout "+timeoutMs);
	}
	
	
	@Override
	public synchronized void becomeLeader() {
		System.out.println("Voted leader: "+self+" in election "+electionNumber);
		super.becomeLeader();
	}

	
}
