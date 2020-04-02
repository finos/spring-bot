package com.github.deutschebank.symphony.stream.cluster.voting;

import java.util.function.Consumer;

import com.github.deutschebank.symphony.stream.cluster.messages.VoteResponse;
import com.github.deutschebank.symphony.stream.msg.Participant;

/**
 * Invokes the "win" method on the client if a majority of votes arrive 
 * for this member of the cluster.  This means clusters must be of odd-sizes,
 * to ensure there is always a majority to be had.
 * 
 * @author robmoffat
 */
public abstract class MajorityDecider implements Consumer<VoteResponse> {
	
	float votes;
	float quorumSize;
	Participant self;
	boolean finished = false;

	public MajorityDecider(int quorumSize, Participant self, float votes) {
		this.votes = votes;
		this.self = self;
		this.quorumSize = quorumSize;
		checkWin();
	}
	
	@Override
	public synchronized void accept(VoteResponse t) {
		if (t.getCandidate().equals(self)) {
			votes += t.getVotes();
			checkWin();
		}
	}

	protected void checkWin() {
		if (votes > (quorumSize / 2f)) {
			if (!finished) {
				finished = true;
				win();
			}
		}
	}
	
	protected abstract void win();
	
}
