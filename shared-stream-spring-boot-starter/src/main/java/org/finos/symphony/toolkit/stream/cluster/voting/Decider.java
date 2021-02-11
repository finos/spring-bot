package org.finos.symphony.toolkit.stream.cluster.voting;

import java.util.function.Consumer;

import org.finos.symphony.toolkit.stream.Participant;
import org.finos.symphony.toolkit.stream.cluster.ClusterMember;
import org.finos.symphony.toolkit.stream.cluster.messages.ClusterMessage;
import org.finos.symphony.toolkit.stream.cluster.messages.SuppressionMessage;
import org.finos.symphony.toolkit.stream.cluster.messages.VoteRequest;

public interface Decider {

	Consumer<ClusterMessage> createDecider(ClusterMember cm, Runnable r); 
	
	Participant voteFor(VoteRequest vr);

	boolean canSuppressWith(ClusterMember inCluster, SuppressionMessage sm);
}
