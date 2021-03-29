package org.finos.symphony.toolkit.stream.fixture;

import org.finos.symphony.toolkit.stream.Participant;
import org.finos.symphony.toolkit.stream.cluster.ClusterMemberImpl;
import org.finos.symphony.toolkit.stream.cluster.HealthSupplier;
import org.finos.symphony.toolkit.stream.cluster.LeaderService;
import org.finos.symphony.toolkit.stream.cluster.Multicaster;
import org.finos.symphony.toolkit.stream.cluster.messages.ClusterMessage;

public class TestClusterMember extends ClusterMemberImpl {
	
	public TestClusterMember(Participant self, long timeoutMs, Multicaster multicaster, HealthSupplier hs,  LeaderService ls) {
		super("test", self, timeoutMs, multicaster, hs, ls);
		System.out.println("Created "+self+" with timeout "+timeoutMs);
	}

//	
//	@Override
//	public synchronized void becomeLeader() {
//		super.becomeLeader();
//	}
//
//
//	@Override
//	public State getState() {
//		State out = super.getState();
//		return out;
//	}
//
//
//	@Override
//	public synchronized ClusterMessage receiveMessage(ClusterMessage cm) {
//		//System.out.println(self+" Received ping from "+cm);
//		return super.receiveMessage(cm);
//	}
//
//	
	
}
