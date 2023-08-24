package org.finos.springbot.workflow.response.handlers;

import java.util.function.Function;

import org.finos.springbot.workflow.response.Response;
import org.springframework.core.PriorityOrdered;

public interface ResponseHandler<R> extends Function<Response, R>, PriorityOrdered {

	public final int MEDIUM_PRIORITY = 100;
	public final int LOW_PRIORITY = 1000;
	
}
