package org.finos.symphony.toolkit.workflow.java.converters;

import org.finos.symphony.toolkit.workflow.response.handlers.ResponseHandlers;

public abstract class AbstractResponseConverter implements ResponseConverter {
	
	protected final ResponseHandlers rh;

	public AbstractResponseConverter(ResponseHandlers rh) {
		super();
		this.rh = rh;
	}
	
}
