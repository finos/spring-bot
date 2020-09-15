package com.github.deutschebank.symphony.workflow.java;

import com.github.deutschebank.symphony.workflow.Workflow;

public interface ConfigurableWorkflow extends Workflow{
	
	public void addClass(Class<?> c);
}
