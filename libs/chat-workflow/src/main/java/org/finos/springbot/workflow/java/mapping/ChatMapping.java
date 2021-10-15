package org.finos.springbot.workflow.java.mapping;

import org.finos.springbot.workflow.actions.Action;
import org.finos.springbot.workflow.annotations.WorkMode;

public interface ChatMapping<T> {

	T getMapping();

	ChatHandlerMethod getHandlerMethod();
	
	ChatHandlerExecutor getExecutor(Action a);

	boolean isButtonFor(Object o, WorkMode m);
	
	String getUniqueName();
}