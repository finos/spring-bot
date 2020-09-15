package com.github.deutschebank.symphony.workflow.response;

import com.github.deutschebank.symphony.workflow.Workflow;
import com.github.deutschebank.symphony.workflow.content.Addressable;

public interface Response {

	/**
	 * Details where the response is supposed to be sent. 
	 */
	public Addressable getAddress(); 
	
	public Workflow getWorkflow();
	
	public String getName();
	
	public String getInstructions();
	
}