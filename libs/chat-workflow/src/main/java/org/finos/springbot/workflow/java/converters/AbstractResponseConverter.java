package org.finos.springbot.workflow.java.converters;

import org.finos.springbot.workflow.response.handlers.ResponseHandlers;

public abstract class AbstractResponseConverter implements ResponseConverter {
	
	protected final ResponseHandlers rh;

	public AbstractResponseConverter(ResponseHandlers rh) {
		super();
		this.rh = rh;
	}
	
}
