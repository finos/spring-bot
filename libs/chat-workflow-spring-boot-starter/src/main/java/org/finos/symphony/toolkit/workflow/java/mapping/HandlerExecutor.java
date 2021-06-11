package org.finos.symphony.toolkit.workflow.java.mapping;

import java.util.Map;

import org.finos.symphony.toolkit.workflow.annotations.ChatVariable;
import org.finos.symphony.toolkit.workflow.content.Content;
import org.springframework.web.method.HandlerMethod;

public interface HandlerExecutor {

	public Map<ChatVariable, Content> getReplacements();
	
	public HandlerMethod method();
	
	public void execute();
}
