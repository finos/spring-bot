package org.finos.symphony.toolkit.workflow.java.resolvers;

import org.finos.symphony.toolkit.workflow.Action;

/**
 * Returns a new workflow resolver, based on given arguments.
 * 
 * @author moffrob
 *
 */
public interface WorkflowResolverFactory {
	
	public static final int LOW_PRIORITY  = 100;
	public static final int NORMAL_PRIORITY  = 50;
	
	public default int priority() {
		return NORMAL_PRIORITY;
	}

	public WorkflowResolver createResolver(Action originatingAction);
}
