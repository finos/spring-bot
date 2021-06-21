package org.finos.symphony.toolkit.workflow.java.mapping;

import org.finos.symphony.toolkit.workflow.Action;

public interface ChatMapping<T> {

	T getMapping();

	ChatHandlerMethod getHandlerMethod();
	
	ChatHandlerExecutor matches(Action a);

}