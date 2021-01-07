package org.finos.symphony.toolkit.stream.filter;

import org.finos.symphony.toolkit.stream.handler.SymphonyStreamHandler;

/**
 * This class takes an existing {@link SymphonyStreamHandler} instance and filters it so that it manages leader election.
 * 
 * Override this bean with your own behaviour if required.
 * 
 * @author rob@kite9.com
 *
 */
public interface LeaderElectionInjector {
	
	public void injectLeaderElectionBehaviour(SymphonyStreamHandler h);
		
}
