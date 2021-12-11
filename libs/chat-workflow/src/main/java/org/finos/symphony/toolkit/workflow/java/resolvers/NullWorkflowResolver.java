package org.finos.symphony.toolkit.workflow.java.resolvers;

import java.util.Optional;

import org.springframework.core.MethodParameter;

final class NullWorkflowResolver implements WorkflowResolver {
	
	@Override
	public boolean canResolve(MethodParameter c) {
		return false;
	}

	@Override
	public Optional<Object> resolve(MethodParameter mp) {
		return Optional.empty();
	}
}