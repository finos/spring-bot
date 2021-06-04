package org.finos.symphony.toolkit.workflow.java.mapping;

import org.finos.symphony.toolkit.workflow.Action;
import org.springframework.web.method.HandlerMethod;

public interface Mapping<T> {

	T getMapping();

	HandlerMethod getHandlerMethod();
	
	boolean matches(Action a);

}