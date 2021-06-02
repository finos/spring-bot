package org.finos.symphony.toolkit.workflow.java.mapping;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.finos.symphony.toolkit.workflow.java.Exposed;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.stereotype.Controller;

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
	public Set<String> getPaths(Exposed mapping) {
		return new HashSet<String>(Arrays.asList(mapping.value()));
	}

}
