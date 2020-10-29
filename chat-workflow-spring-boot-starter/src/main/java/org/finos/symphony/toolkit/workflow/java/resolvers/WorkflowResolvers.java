package org.finos.symphony.toolkit.workflow.java.resolvers;

import java.util.Optional;

import org.finos.symphony.toolkit.workflow.content.Addressable;

/**
 * Provides a plugable way to resolve method parameters, in a given {@link Addressable} (room or IM).
 * 
 * @author moffrob
 *
 */
public interface WorkflowResolvers {

	public boolean canResolve(Class<?> c);
	
	public Optional<Object> resolve(Class<?> c, Addressable a, boolean isTarget);
	
}
