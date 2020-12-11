package org.finos.symphony.toolkit.stream.cluster.transport;

import java.util.function.Consumer;

import org.finos.symphony.toolkit.stream.Participant;
import org.finos.symphony.toolkit.stream.cluster.messages.ClusterMessage;

/**
 * Sends a message to other members of the cluster.
 * 
 * @author robmoffat
 *
 */
public interface Multicaster extends Consumer<Participant> {

	public void sendAsyncMessage(Participant from, ClusterMessage cm, Consumer<ClusterMessage> responsesConsumer); 

	public int getQuorumSize();
	
}
