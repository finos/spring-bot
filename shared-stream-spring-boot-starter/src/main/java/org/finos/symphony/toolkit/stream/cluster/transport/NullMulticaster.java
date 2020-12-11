package org.finos.symphony.toolkit.stream.cluster.transport;

import java.util.function.Consumer;

import org.finos.symphony.toolkit.stream.Participant;
import org.finos.symphony.toolkit.stream.cluster.messages.ClusterMessage;

/**
 * This class can be used in the case where we don't have a proper way of communicating with other cluster members
 * (HttpMulticaster being the obvious one).
 * @author moffrob
 *
 */
public class NullMulticaster implements Multicaster {

	@Override
	public void accept(Participant arg0) {
	}

	@Override
	public void sendAsyncMessage(Participant from, ClusterMessage cm, Consumer<ClusterMessage> responsesConsumer) {
	}

	@Override
	public int getQuorumSize() {
		return 1;
	}

}
