package org.finos.symphony.toolkit.workflow.java.mapping;

import java.lang.reflect.Type;
import java.util.Optional;

import org.finos.symphony.toolkit.workflow.content.Addressable;
import org.finos.symphony.toolkit.workflow.java.resolvers.WorkflowResolver;

public class ChatVariableWorkflowResolver implements WorkflowResolver {
	
	

	@Override
	public boolean canResolve(Type t) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Optional<Object> resolve(Type t, Addressable a) {
		// TODO Auto-generated method stub
		return null;
	}

}
