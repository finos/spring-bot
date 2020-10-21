package com.github.deutschebank.symphony.workflow.java.resolvers;

import java.util.Optional;

import com.github.deutschebank.symphony.workflow.content.Addressable;

/**
 * Provides a plugable way to resolve method parameters, in a given {@link Addressable} (room or IM).
 * 
 * @author moffrob
 *
 */
public interface WorkflowResolver {

	public boolean canResolve(Class<?> c);
	
	/**
	 * Resolves an argument for a method call, within a given room a, of type c. 
	 * If we are resolving the object that the method is being called on, isTarget=true.
	 */
	public Optional<Object> resolve(Class<?> c, Addressable a, boolean isTarget);
	
}

