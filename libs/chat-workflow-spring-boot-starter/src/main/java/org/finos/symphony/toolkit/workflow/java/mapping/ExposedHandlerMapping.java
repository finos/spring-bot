package org.finos.symphony.toolkit.workflow.java.mapping;

import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;

import org.finos.symphony.toolkit.json.EntityJson;
import org.finos.symphony.toolkit.workflow.Action;
import org.finos.symphony.toolkit.workflow.content.Message;
import org.finos.symphony.toolkit.workflow.java.Exposed;
import org.finos.symphony.toolkit.workflow.sources.symphony.elements.ElementsAction;
import org.finos.symphony.toolkit.workflow.sources.symphony.messages.SimpleMessageAction;
import org.finos.symphony.toolkit.workflow.sources.symphony.messages.SimpleMessageParser;
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
				if (a instanceof ElementsAction) {
					return matchesElementsAction((ElementsAction)a);
				}
				
				if (a instanceof SimpleMessageAction) {
					return matchesSimpleMessageAction((SimpleMessageAction)a);
				}
				
				return false;
			}

			private boolean matchesSimpleMessageAction(SimpleMessageAction a) {
				Exposed e = getMapping();
				
				if (!e.isMessage()) {
					return false;
				}
				
				for (String path : e.value()) {
					if (pathMatches(path, a.getWords(), e)) {
						return true;
					}
				}
				
				return false;
			}

			private boolean pathMatches(String path, Message words, Exposed e) {
				try {
					SimpleMessageParser parser = new SimpleMessageParser();
					Message m = parser.parseNaked(path);
					return words.matches(m);
				} catch (Exception e1) {
					throw new IllegalArgumentException(path);
				}
			}

			private boolean matchesElementsAction(ElementsAction a) {
				Exposed e = getMapping();
				
				if (!e.isButton()) {
					return false;
				}
				
				if (e.formClass() != null) {
					Class<?> expectedFormClass = e.formClass();
					if (!expectedFormClass.isAssignableFrom(a.getFormData().getClass())) {
						return false;
					}
				}
			}
		};
	}


}
