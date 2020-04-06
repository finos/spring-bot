package com.github.deutschebank.symphony.stream.cluster.voting;

import java.util.function.Consumer;

import com.github.deutschebank.symphony.stream.Participant;
import com.github.deutschebank.symphony.stream.cluster.messages.SuppressionMessage;
import com.github.deutschebank.symphony.stream.cluster.messages.VoteRequest;
import com.github.deutschebank.symphony.stream.cluster.messages.VoteResponse;

public interface Decider {

	Consumer<VoteResponse> createDecider(Runnable r); 
	
	Participant voteFor(VoteRequest vr);

	boolean canSuppressWith(SuppressionMessage sm);
}
