package com.github.deutschebank.symphony.stream.cluster.voting;

import java.util.function.Consumer;

import com.github.deutschebank.symphony.stream.cluster.messages.VoteResponse;
import com.github.deutschebank.symphony.stream.msg.Participant;


public abstract class VoteCounter implements Consumer<VoteResponse> {
	
	float votes;
	float quorumSize;
	Participant self;
	boolean finished = false;

	public VoteCounter(int quorumSize, Participant self, float votes) {
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
