package com.github.deutschebank.symphony.stream.cluster.voting;

import java.util.function.Consumer;

import com.github.deutschebank.symphony.stream.Participant;
import com.github.deutschebank.symphony.stream.cluster.messages.SuppressionMessage;
import com.github.deutschebank.symphony.stream.cluster.messages.VoteRequest;
import com.github.deutschebank.symphony.stream.cluster.messages.VoteResponse;

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
	public Consumer<VoteResponse> createDecider(Runnable r) {
		r.run();
		return (vr) -> {};
	}

	@Override
	public Participant voteFor(VoteRequest vr) {
		return self;
	}

	@Override
	public boolean canSuppressWith(SuppressionMessage sm) {
		int selfValue = Math.abs(self.hashCode());
		int contenderValue = Math.abs(sm.getLeader().hashCode());
		boolean out = contenderValue > selfValue;
		System.out.println(self+" has value "+selfValue+" vs "+sm.getLeader()+" has value "+contenderValue+" suppresed:"+out);
		return out;
	}

}
