package org.finos.symphony.toolkit.workflow.java.workflow;

import org.finos.symphony.toolkit.workflow.Workflow;

public interface ConfigurableWorkflow extends Workflow {
	
	/**
	 * Adds a class to the workflow.  This registers the class so we can 
	 * de/serialize it and call methods on it.
	 */
	public void addClass(Class<?> c);
	
}
