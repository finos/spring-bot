package org.finos.symphony.toolkit.stream.cluster.transport;

import java.util.List;
import java.util.function.Consumer;

import org.finos.symphony.toolkit.stream.Participant;
import org.finos.symphony.toolkit.stream.cluster.messages.ClusterMessage;

/**
 * Sends a message to other members of the cluster.
 * 
 * @author robmoffat
 *
 */
public interface Multicaster {

	public void sendAsyncMessage(Participant from, List<Participant> to, ClusterMessage cm, Consumer<ClusterMessage> responsesConsumer); 
	
}
