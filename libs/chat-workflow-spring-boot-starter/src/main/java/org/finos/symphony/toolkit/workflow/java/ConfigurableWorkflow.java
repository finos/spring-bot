package org.finos.symphony.toolkit.workflow.java;

import org.finos.symphony.toolkit.workflow.Workflow;

public interface ConfigurableWorkflow extends Workflow{
	
	public void addClass(Class<?> c);
}
