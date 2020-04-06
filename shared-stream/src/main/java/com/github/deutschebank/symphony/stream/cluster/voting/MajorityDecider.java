package com.github.deutschebank.symphony.stream.cluster.voting;

import java.util.function.Consumer;
import java.util.function.Supplier;

import com.github.deutschebank.symphony.stream.Participant;
import com.github.deutschebank.symphony.stream.cluster.messages.ClusterMessage;
import com.github.deutschebank.symphony.stream.cluster.messages.SuppressionMessage;
import com.github.deutschebank.symphony.stream.cluster.messages.VoteRequest;
import com.github.deutschebank.symphony.stream.cluster.messages.VoteResponse;

/**
 * Invokes the "win" method on the client if a majority of votes arrive 
 * for this member of the cluster.  This means clusters must be of odd-sizes,
 * to ensure there is always a majority to be had.
 * 
 * @author robmoffat
 */
public class MajorityDecider implements Decider {
	
	private final Participant self;
	private final Supplier<Integer> quorumSize;
	
	public MajorityDecider(Supplier<Integer> quorumSize, Participant self) {
		super();
		this.self = self;
		this.quorumSize = quorumSize;
	}
	
	class MajorityConsumer implements Consumer<ClusterMessage> {
		
		int votes = 1;
		boolean finished = false;
		Runnable r;
		
		public MajorityConsumer(int votes, boolean finished, Runnable r) {
			super();
			this.votes = votes;
			this.finished = finished;
			this.r = r;
			checkWin();
		}

		@Override
		public void accept(ClusterMessage t) {
			if ((t instanceof VoteResponse) && (((VoteResponse) t).getCandidate().equals(self))) {
				votes += ((VoteResponse) t).getVotes();
				checkWin();
			}
		}
		
		protected void checkWin() {
			if (votes > (quorumSize.get() / 2f)) {
				if (!finished) {
					finished = true;
					r.run();
				}
			}
		}
	}
		
	

	@Override
	public Consumer<ClusterMessage> createDecider(Runnable r) {
		return new MajorityConsumer(1, false, r);
	}

	@Override
	public Participant voteFor(VoteRequest vr) {
		return vr.getCandidate();
	}

	@Override
	public boolean canSuppressWith(SuppressionMessage sm) {
		return true;
	}
}
