package org.finos.symphony.toolkit.workflow.java.resolvers;

import org.finos.symphony.toolkit.workflow.Action;

/**
 * Returns a new workflow resolver, based on given arguments.
 * 
 * @author moffrob
 *
 */
public interface WorkflowResolverFactory {

	public WorkflowResolver createResolver(Action originatingAction);
}
