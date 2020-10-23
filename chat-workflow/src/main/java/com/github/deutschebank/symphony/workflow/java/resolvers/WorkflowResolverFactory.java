package com.github.deutschebank.symphony.workflow.java.resolvers;

import com.github.deutschebank.symphony.workflow.Action;

/**
 * Returns a new workflow resolver, based on given arguments.
 * 
 * @author moffrob
 *
 */
public interface WorkflowResolverFactory {

	public WorkflowResolver createResolver(Action originatingAction);
}
