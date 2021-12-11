package org.finos.symphony.toolkit.workflow.java.mapping;

import java.util.Map;

import org.finos.symphony.toolkit.workflow.actions.Action;
import org.finos.symphony.toolkit.workflow.annotations.ChatVariable;

public interface ChatHandlerExecutor {

	public Map<ChatVariable, Object> getReplacements();
		
	public Action action();
	
	public void execute() throws Throwable;
	
	public ChatMapping<?> getOriginatingMapping();
	
}
