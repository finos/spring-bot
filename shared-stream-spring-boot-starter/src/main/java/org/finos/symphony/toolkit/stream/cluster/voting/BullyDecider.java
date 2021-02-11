package org.finos.symphony.toolkit.stream.cluster.voting;

import java.util.function.Consumer;

import org.finos.symphony.toolkit.stream.Participant;
import org.finos.symphony.toolkit.stream.cluster.ClusterMember;
import org.finos.symphony.toolkit.stream.cluster.messages.ClusterMessage;
import org.finos.symphony.toolkit.stream.cluster.messages.SuppressionMessage;
import org.finos.symphony.toolkit.stream.cluster.messages.VoteRequest;
import org.finos.symphony.toolkit.stream.cluster.messages.VoteResponse;

/**
 * Bully doesn't care about votes, he just declares himself the winner of the election.
 * 
 */
public class BullyDecider implements Decider {
	
	Participant self;
	
	public BullyDecider(Participant self) {
		super();
		this.self = self;
	}

	class BullyConsumer implements Consumer<VoteResponse> {
		
		BullyConsumer(Runnable r) {
			
		}

		@Override
		public void accept(VoteResponse t) {
		}
		
	}

	@Override
	public Consumer<ClusterMessage> createDecider(ClusterMember cm, Runnable r) {
		r.run();
		return (vr) -> {};
	}

	@Override
	public Participant voteFor(VoteRequest vr) {
		return self;
	}

	@Override
	public boolean canSuppressWith(ClusterMember cm, SuppressionMessage sm) {
		int selfValue = Math.abs(self.hashCode());
		int contenderValue = Math.abs(sm.getLeader().hashCode());
		boolean out = contenderValue > selfValue;
		
		System.out.println("self: "+selfValue+" contender: "+contenderValue+" "+(out ? "WIN" : "LOSE"));
		
		return out;
	}

}
