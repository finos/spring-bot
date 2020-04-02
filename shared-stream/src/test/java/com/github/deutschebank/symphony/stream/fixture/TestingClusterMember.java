package com.github.deutschebank.symphony.stream.fixture;

import java.util.function.Consumer;

import com.github.deutschebank.symphony.stream.cluster.AbstractRaftClusterMember;
import com.github.deutschebank.symphony.stream.cluster.messages.ClusterMessage;
import com.github.deutschebank.symphony.stream.msg.Participant;

public class TestingClusterMember extends AbstractRaftClusterMember {
	
	private TestNetwork n;
	private int clusterSize;

	public TestingClusterMember(String memberName, Participant self, long timeoutMs, TestNetwork n, int clusterSize) {
		super(memberName, self, timeoutMs);
		this.n = n;
		n.register(self, this);
		this.clusterSize = clusterSize;
		System.out.println(memberName+" with timeout "+timeoutMs);
	}
	
	
	protected <R extends ClusterMessage> void sendAsyncMessage(ClusterMessage cm, Consumer<R> consumer) {
		for (Participant p : n.getParticipants()) {
			if (p != self) {
				sendAsyncMessage(p, cm, consumer);
			}
		}
	} 

	protected <REQ extends ClusterMessage, RES extends ClusterMessage> 
		void sendAsyncMessage(Participant to, REQ r, Consumer<RES> consumer) {
		n.sendMessage(self, to, r, consumer);
	}


	@Override
	protected int getClusterSize() {
		return clusterSize;
	}


	@Override
	public synchronized void becomeLeader() {
		System.out.println("Voted leader: "+self+" in election "+electionNumber);
		super.becomeLeader();
	}

	
}
