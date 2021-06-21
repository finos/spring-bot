package org.finos.symphony.toolkit.workflow.java.resolvers;

import java.util.Optional;

import org.finos.symphony.toolkit.workflow.content.Addressable;
import org.springframework.core.MethodParameter;

/**
 * Provides a plugable way to resolve method parameters, in a given {@link Addressable} (room or IM).
 * 
 * @author moffrob
 *
 */
public interface WorkflowResolvers {

	public boolean canResolve(MethodParameter t);
	
	public Optional<Object> resolve(MethodParameter mp);
	
}
