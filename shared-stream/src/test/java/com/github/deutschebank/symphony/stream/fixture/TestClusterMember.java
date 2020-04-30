package com.github.deutschebank.symphony.stream.fixture;

import com.github.deutschebank.symphony.stream.Participant;
import com.github.deutschebank.symphony.stream.cluster.RaftClusterMember;
import com.github.deutschebank.symphony.stream.cluster.transport.Multicaster;
import com.github.deutschebank.symphony.stream.cluster.voting.Decider;

public class TestClusterMember extends RaftClusterMember {
	
	
	
	public TestClusterMember(Participant self, long timeoutMs, Decider d, Multicaster multicaster) {
		super(self, timeoutMs, d, multicaster);
	}


	public TestClusterMember(Participant self, long timeoutMs, TestNetwork n, Decider d) {
		super(self,timeoutMs, d, n);
		n.register(self, this);
		// System.out.println(self+" with timeout "+timeoutMs);
	}
	
	
	@Override
	public synchronized void becomeLeader() {
		System.out.println("Voted leader: "+self+" in election "+electionNumber);
		super.becomeLeader();
	}

	
}
