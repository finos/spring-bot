package org.finos.symphony.toolkit.workflow.java.converters;

import org.finos.symphony.toolkit.workflow.java.mapping.ChatHandlerExecutor;
import org.finos.symphony.toolkit.workflow.response.Response;
import org.springframework.core.PriorityOrdered;

public interface ResponseConverter extends PriorityOrdered {

	public boolean canConvert(Object in);

	public Response convert(Object source, ChatHandlerExecutor creator);
}
