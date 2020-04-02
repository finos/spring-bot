package com.github.deutschebank.symphony.stream.cluster.voting;

import java.util.function.Consumer;

import com.github.deutschebank.symphony.stream.Participant;
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
	
	private final float quorumSize;
	private final Participant self;
	
	public MajorityDecider(float quorumSize, Participant self) {
		super();
		this.quorumSize = quorumSize;
		this.self = self;
	}

	class MajorityConsumer implements Consumer<VoteResponse> {
		
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
		public void accept(VoteResponse t) {
			if (t.getCandidate().equals(self)) {
				votes += t.getVotes();
				checkWin();
			}
		}
		
		protected void checkWin() {
			if (votes > (quorumSize / 2f)) {
				if (!finished) {
					finished = true;
					r.run();
				}
			}
		}
	}
		
	

	@Override
	public Consumer<VoteResponse> createDecider(Runnable r) {
		return new MajorityConsumer(1, false, r);
	}

	@Override
	public Participant voteFor(VoteRequest vr) {
		return vr.getCandidate();
	}
}
