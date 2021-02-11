package org.finos.symphony.toolkit.stream.fixture;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Consumer;

import org.finos.symphony.toolkit.stream.Participant;
import org.finos.symphony.toolkit.stream.cluster.ClusterMember;
import org.finos.symphony.toolkit.stream.cluster.messages.ClusterMessage;
import org.finos.symphony.toolkit.stream.cluster.transport.Multicaster;

public class TestNetwork implements Multicaster {
	
	private Map<Participant, ClusterMember> members = new HashMap<>();
	private Connectivity c;
	int maxDelay;
	
	public TestNetwork(Connectivity c, int maxDelay) {
		super();
		this.c = c;
		this.maxDelay = maxDelay;
	}

	public void register(Participant p, TestClusterMember tcm) {
		members.put(p, tcm);
	}

	public void sendMessage(Participant from, Participant to, ClusterMessage r, Consumer<ClusterMessage> consumer) {
		if (members.containsKey(to)) {
			ClusterMember tcm = members.get(to);
						
			Thread nw = new Thread(() -> {
				randomDelay();
				
				//System.out.println("Attempting to send message from "+from+" to "+to);
				
				if (c.canTalkTo(from, to)) {
					ClusterMessage vr = tcm.receiveMessage(r);
					randomDelay();

					//System.out.println("Sent message from "+from+" to "+to);

					consumer.accept(vr);
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

	@Override
	public void sendAsyncMessage(Participant self, List<Participant> to, ClusterMessage cm, Consumer<ClusterMessage> responsesConsumer) {
		for (Participant p : to) {
			if (p != self) {
				sendMessage(self, p, cm, responsesConsumer);
			}
		}
	} 

}
