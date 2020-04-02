package com.github.deutschebank.symphony.stream.fixture;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Consumer;

import com.github.deutschebank.symphony.stream.cluster.ClusterMember;
import com.github.deutschebank.symphony.stream.cluster.messages.ClusterMessage;
import com.github.deutschebank.symphony.stream.cluster.messages.SuppressionMessage;
import com.github.deutschebank.symphony.stream.cluster.messages.VoteRequest;
import com.github.deutschebank.symphony.stream.cluster.messages.VoteResponse;
import com.github.deutschebank.symphony.stream.msg.Participant;

public class TestNetwork {
	
	private Map<Participant, ClusterMember> members = new HashMap<>();
	private Connectivity c;
	int maxDelay;
	
	public TestNetwork(Connectivity c, int maxDelay) {
		super();
		this.c = c;
		this.maxDelay = maxDelay;
	}

	public void register(Participant p, TestingClusterMember tcm) {
		members.put(p, tcm);
	}

	@SuppressWarnings("unchecked")
	public <REQ extends ClusterMessage, RES extends ClusterMessage> void sendMessage(Participant from, Participant to, REQ r, Consumer<RES> consumer) {
		if (members.containsKey(to)) {
			ClusterMember tcm = members.get(to);
			
			Thread nw = new Thread(() -> {
				randomDelay();
				
				if (c.canTalkTo(from, to)) {
					if (r instanceof VoteRequest) {
						VoteResponse vr = tcm.receiveVoteRequest((VoteRequest) r);
						randomDelay();
						consumer.accept((RES) vr);
					} else if (r instanceof SuppressionMessage) {
						tcm.receivePing((SuppressionMessage) r);
					}	
				}
				
			});
			
			nw.setName("network event");
			
			nw.start();
		}
	}
	
	private void randomDelay() {
		try {
			Thread.sleep(new Random().nextInt(maxDelay));
		} catch (InterruptedException e) {
		}
	}

	public Collection<ClusterMember> getMembers() {
		return members.values();
	}
	
	public Set<Participant> getParticipants() {
		return members.keySet();
	}

}
