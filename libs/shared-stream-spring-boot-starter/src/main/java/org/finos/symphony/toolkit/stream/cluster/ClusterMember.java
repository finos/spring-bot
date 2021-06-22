package org.finos.symphony.toolkit.stream.cluster;

import org.finos.symphony.toolkit.stream.Participant;
import org.finos.symphony.toolkit.stream.cluster.messages.ClusterMessage;

/**
 * The cluster has only a single "active" member, which is defined as the last person to 
 * have written a Leader message into the coordination room.  Other members of the cluster
 * are "suppressed" by the leader, until the leader can no-longer communicate with symphony.
 * 
 * @author robmoffat
 *
 */
public interface ClusterMember {
	
	public enum State { LEADER, SUPRESSED, INOPERABLE, STOPPED }

	public void startup();
	
	public void shutdown();
	
	public ClusterMessage receiveMessage(ClusterMessage cm);
	
	public Participant getSelfDetails();
	
	public State getState();
	
	public String getClusterName();
	
}
