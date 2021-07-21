package org.finos.symphony.toolkit.workflow.java.mapping;

import org.finos.symphony.toolkit.workflow.actions.Action;
import org.finos.symphony.toolkit.workflow.annotations.WorkMode;

public interface ChatMapping<T> {

	T getMapping();

	ChatHandlerMethod getHandlerMethod();
	
	ChatHandlerExecutor getExecutor(Action a);

	boolean isButtonFor(Object o, WorkMode m);
}