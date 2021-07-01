package org.finos.symphony.toolkit.workflow.java.mapping;

import java.util.List;

import org.finos.symphony.toolkit.workflow.actions.Action;

/**
 * When an action occurs, this finds out whether we have any handlers for it, and returns executors for them.
 *  
 * @author rob@kite9.com
 *
 */
public interface ChatHandlerMapping<T> {

	public List<ChatMapping<T>> getHandlers(Action a);
	
	public List<ChatHandlerExecutor> getExecutors(Action a);
	
}
