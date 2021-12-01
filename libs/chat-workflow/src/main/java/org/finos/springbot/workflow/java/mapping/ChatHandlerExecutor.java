package org.finos.springbot.workflow.java.mapping;

import java.util.Map;

import org.finos.springbot.workflow.actions.Action;
import org.finos.springbot.workflow.annotations.ChatVariable;

public interface ChatHandlerExecutor {

	public Map<ChatVariable, Object> getReplacements();
		
	public Action action();
	
	public void execute() throws Throwable;
	
	public ChatMapping<?> getOriginatingMapping();
	
}
