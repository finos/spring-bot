package org.finos.symphony.toolkit.workflow.java.mapping;

import org.finos.symphony.toolkit.workflow.actions.Action;

public interface ChatMapping<T> {

	T getMapping();

	ChatHandlerMethod getHandlerMethod();
	
	ChatHandlerExecutor getExecutor(Action a);

}