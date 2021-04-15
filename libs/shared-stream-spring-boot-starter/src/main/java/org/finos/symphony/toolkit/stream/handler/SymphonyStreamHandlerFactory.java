package org.finos.symphony.toolkit.stream.handler;

import java.util.Collection;
import java.util.List;

import org.finos.symphony.toolkit.spring.api.factories.ApiInstance;
import org.finos.symphony.toolkit.stream.StreamEventConsumer;
import org.finos.symphony.toolkit.stream.cluster.ClusterMember;

/**
 * Constructs a stream handler, for a given set of {@link StreamEventConsumer}s and an {@link ApiInstance}.
 * i.e. links up bot identities with handlers.
 * 
 * @author rob@kite9.com
 *
 */
public interface SymphonyStreamHandlerFactory {
			
	public SymphonyStreamHandler createBean(ApiInstance symphonyApi, List<StreamEventConsumer> consumers);
	
	public Collection<SymphonyStreamHandler> getAllHandlers();
	
	public Collection<ClusterMember> allClusterMembers();
	
	public void stopAll();
}