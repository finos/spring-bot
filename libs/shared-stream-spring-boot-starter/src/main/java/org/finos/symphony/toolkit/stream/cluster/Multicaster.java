package org.finos.symphony.toolkit.stream.cluster;

import java.util.List;

import org.finos.symphony.toolkit.stream.Participant;
import org.finos.symphony.toolkit.stream.cluster.messages.ClusterMessage;

/**
 * Sends a message to other members of the cluster.
 * 
 * @author robmoffat
 *
 */
public interface Multicaster {

	public void sendAsyncMessage(Participant from, List<Participant> to, ClusterMessage cm); 
	
}
