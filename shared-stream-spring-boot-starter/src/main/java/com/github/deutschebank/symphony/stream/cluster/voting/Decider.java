package com.github.deutschebank.symphony.stream.cluster.voting;

import java.util.function.Consumer;

import com.github.deutschebank.symphony.stream.Participant;
import com.github.deutschebank.symphony.stream.cluster.messages.ClusterMessage;
import com.github.deutschebank.symphony.stream.cluster.messages.SuppressionMessage;
import com.github.deutschebank.symphony.stream.cluster.messages.VoteRequest;

public interface Decider {

	Consumer<ClusterMessage> createDecider(Runnable r); 
	
	Participant voteFor(VoteRequest vr);

	boolean canSuppressWith(SuppressionMessage sm);
}
