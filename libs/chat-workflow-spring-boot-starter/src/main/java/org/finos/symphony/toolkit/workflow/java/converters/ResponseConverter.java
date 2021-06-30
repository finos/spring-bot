package org.finos.symphony.toolkit.workflow.java.converters;

import org.finos.symphony.toolkit.workflow.java.mapping.ChatHandlerExecutor;
import org.finos.symphony.toolkit.workflow.response.Response;

public interface ResponseConverter {

	public boolean canConvert(Object in);

	public Response convert(Object source, ChatHandlerExecutor creator);
}
