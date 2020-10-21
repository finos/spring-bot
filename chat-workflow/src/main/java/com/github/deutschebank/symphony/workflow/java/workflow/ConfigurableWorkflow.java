package com.github.deutschebank.symphony.workflow.java.workflow;

import com.github.deutschebank.symphony.workflow.Workflow;

public interface ConfigurableWorkflow extends Workflow {
	
	/**
	 * Adds a class to the workflow.  This registers the class so we can 
	 * de/serialize it and call methods on it.
	 */
	public void addClass(Class<?> c);
	
}
