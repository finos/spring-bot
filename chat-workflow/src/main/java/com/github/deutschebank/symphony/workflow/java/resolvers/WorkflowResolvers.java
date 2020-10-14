package com.github.deutschebank.symphony.workflow.java.resolvers;

import java.util.Optional;

import com.github.deutschebank.symphony.workflow.content.Addressable;

/**
 * Provides a plugable way to resolve method parameters, in a given {@link Addressable} (room or IM).
 * 
 * @author moffrob
 *
 */
public interface WorkflowResolvers {

	public boolean canResolve(Class<?> c);
	
	public Optional<Object> resolve(Class<?> c, Addressable a);
	
}
