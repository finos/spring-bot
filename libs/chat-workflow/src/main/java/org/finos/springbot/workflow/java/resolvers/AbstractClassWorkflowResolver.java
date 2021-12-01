package org.finos.springbot.workflow.java.resolvers;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Optional;

import org.finos.springbot.workflow.annotations.ChatVariable;
import org.springframework.core.MethodParameter;

/**
 * This is used when we are trying to resolve an object by it's type,
 * rather than anything else in the {@link MethodParameter}.
 * 
 * This allows us to support Optional<> parameters easily.
 * 
 * @author rob@kite9.com
 *
 */
public abstract class AbstractClassWorkflowResolver implements WorkflowResolver {

	@Override
	public boolean canResolve(MethodParameter mp) {
		ChatVariable chatVariable = mp.getParameterAnnotation(ChatVariable.class);
		if (chatVariable != null) {
			return false;
		}
		
		Type t = mp.getGenericParameterType();
		
		if (isOptional(t)) {
			t = getOptionalType(t);
		}
		
		if (t instanceof Class<?>) {
			return canResolve((Class<?>) t);
		}
		
		return false;
	}

	public static boolean isOptional(Type t) {
		return t.getTypeName().startsWith(Optional.class.getName());
	}
	
	public static Type getOptionalType(Type t) {
		ParameterizedType pt = (ParameterizedType) t;
		Type out = pt.getActualTypeArguments()[0];
		return out;
	}
	
	public abstract boolean canResolve(Class<?> t);

	@Override
	public Optional<Object> resolve(MethodParameter mp) {
		Type t = mp.getGenericParameterType();
 		if (isOptional(t)) {
 			t = getOptionalType(t);
 			
 			if (t instanceof Class<?>) {
 				Optional<Object> out = resolve((Class<?>) t);
 				return Optional.of(out);
 			} else {
 				return Optional.empty();
 			}
 		} else {
 			if (t instanceof Class<?>) {
 				return resolve((Class<?>) t);
 			} else {
 				return Optional.empty();
 			}
 		}
	}

	public abstract Optional<Object> resolve(Class<?> t);
}
