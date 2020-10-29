package org.finos.symphony.toolkit.workflow.response;

import org.finos.symphony.toolkit.workflow.Workflow;
import org.finos.symphony.toolkit.workflow.content.Addressable;

public interface Response {

	/**
	 * Details where the response is supposed to be sent. 
	 */
	public Addressable getAddress(); 
	
	public Workflow getWorkflow();
	
	public String getName();
	
	public String getInstructions();
	
}