package org.finos.symphony.toolkit.workflow.java.mapping;

import java.lang.reflect.Method;

import org.finos.symphony.toolkit.workflow.java.Exposed;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.stereotype.Controller;

public class ExposedHandlerMapping extends AbstractSpringComponentHandlerMapping<Object> {


	@Override
	protected boolean isHandler(Class<?> beanType) {
		return AnnotatedElementUtils.hasAnnotation(beanType, Controller.class);
	}

	@Override
	protected Object getMappingForMethod(Method method, Class<?> handlerType) {
		if (AnnotatedElementUtils.hasAnnotation(method, Exposed.class)) {
			
			
			
		} else {
			return null;
		}
	}

}
