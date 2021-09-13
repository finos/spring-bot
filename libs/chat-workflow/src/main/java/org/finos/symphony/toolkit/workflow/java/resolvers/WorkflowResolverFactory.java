package org.finos.symphony.toolkit.workflow.java.resolvers;

import org.finos.symphony.toolkit.workflow.java.mapping.ChatHandlerExecutor;
import org.springframework.core.PriorityOrdered;

/**
 * Returns a new workflow resolver, based on given arguments.
 * 
 * @author moffrob
 *
 */
public interface WorkflowResolverFactory extends PriorityOrdered {
	
	public static final int LOW_PRIORITY  = 100;
	public static final int NORMAL_PRIORITY  = 50;
	

	@Override
	default int getOrder() {
		return NORMAL_PRIORITY;
	}

	public WorkflowResolver createResolver(ChatHandlerExecutor che);
}
