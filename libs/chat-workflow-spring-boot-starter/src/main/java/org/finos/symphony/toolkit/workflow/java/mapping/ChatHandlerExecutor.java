package org.finos.symphony.toolkit.workflow.java.mapping;

import java.util.Map;

import org.finos.symphony.toolkit.workflow.Action;
import org.finos.symphony.toolkit.workflow.annotations.ChatVariable;
import org.finos.symphony.toolkit.workflow.content.Content;

public interface ChatHandlerExecutor {

	public Map<ChatVariable, Content> getReplacements();
	
	public ChatHandlerMethod getChatHandlerMethod();
	
	public Action action();
	
	public void execute();
}
