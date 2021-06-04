package org.finos.symphony.toolkit.workflow.java.mapping;

import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;

import org.finos.symphony.toolkit.workflow.Action;
import org.finos.symphony.toolkit.workflow.java.Exposed;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.method.HandlerMethod;

public class ExposedHandlerMapping extends AbstractSpringComponentHandlerMapping<Exposed> {


	@Override
	protected boolean isHandler(Class<?> beanType) {
		return AnnotatedElementUtils.hasAnnotation(beanType, Controller.class);
	}

	@Override
	protected Exposed getMappingForMethod(Method method, Class<?> handlerType) {
		if (AnnotatedElementUtils.hasAnnotation(method, Exposed.class)) {
			return AnnotatedElementUtils.getMergedAnnotation(method, Exposed.class);
		} else {
			return null;
		}
	}

	@Override
	public List<Mapping<Exposed>> getHandlers(Action a) {
		mappingRegistry.acquireReadLock();
		
		List<Mapping<Exposed>> out = mappingRegistry.getRegistrations().values().stream()
			.filter(m -> m.matches(a))
			.collect(Collectors.toList());
		
		mappingRegistry.releaseReadLock();
		return out;
	}

	@Override
	protected MappingRegistration<Exposed> createMappingRegistration(Exposed mapping, HandlerMethod handlerMethod) {
		
		
		return new MappingRegistration<Exposed>(mapping, handlerMethod) {

			@Override
			public boolean matches(Action a) {
				// TODO Auto-generated method stub
				return false;
			}
		};
	}


}
