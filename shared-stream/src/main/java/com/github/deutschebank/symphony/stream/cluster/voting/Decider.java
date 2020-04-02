package com.github.deutschebank.symphony.stream.cluster.voting;

import java.util.function.Consumer;

import com.github.deutschebank.symphony.stream.cluster.messages.VoteResponse;

public interface Decider extends Consumer<VoteResponse> {

	public void win(); 
}
