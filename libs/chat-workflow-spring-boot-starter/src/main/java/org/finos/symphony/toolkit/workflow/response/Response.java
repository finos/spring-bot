package org.finos.symphony.toolkit.workflow.response;

import org.finos.symphony.toolkit.workflow.content.Addressable;

public interface Response {
	
	public Addressable getAddress();

	public String getTemplate();
	
}