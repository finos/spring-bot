package org.finos.symphony.toolkit.workflow.java.mapping;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.finos.symphony.toolkit.workflow.Action;
import org.finos.symphony.toolkit.workflow.annotations.ChatVariable;
import org.finos.symphony.toolkit.workflow.content.Content;
import org.finos.symphony.toolkit.workflow.content.Message;
import org.finos.symphony.toolkit.workflow.content.Word;
import org.finos.symphony.toolkit.workflow.java.Exposed;
import org.finos.symphony.toolkit.workflow.java.mapping.WildcardContent.Arity;
import org.finos.symphony.toolkit.workflow.sources.symphony.elements.ElementsAction;
import org.finos.symphony.toolkit.workflow.sources.symphony.messages.SimpleMessageAction;
import org.springframework.core.MethodParameter;
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
			.filter(m -> m.matches(a) != null)
			.collect(Collectors.toList());
		
		mappingRegistry.releaseReadLock();
		return out;
	}
	

	@Override
	public List<HandlerExecutor> getExecutors(Action a) {
		mappingRegistry.acquireReadLock();
		
		List<HandlerExecutor> out = mappingRegistry.getRegistrations().values().stream()
			.map(m -> m.matches(a))
			.filter(f -> f != null)
			.collect(Collectors.toList());
		
		mappingRegistry.releaseReadLock();
		return out;
	}

	protected List<MessageMatcher> createMessageMatchers(Exposed mapping, List<WildcardContent> chatVariables) {
		List<MessageMatcher> parts = Arrays.stream(mapping.value())
				.map(str -> createContentPattern(str, chatVariables))
				.map(cp -> new MessageMatcher(cp))
				.collect(Collectors.toList());
		
		return parts;
	}
	
	private static WildcardContent ANY_CONTENT = new WildcardContent(null, Content.class, Arity.ONE);
	
	
	private Content createContentPattern(String str, List<WildcardContent> chatVariables) {
		List<Content> items = Arrays.stream(str.split("\\s")) 
			.map(word -> {
				if (word.startsWith("{") && word.endsWith("}")) {
					String pathVariableName = word.substring(1, word.length()-1);
					WildcardContent out = chatVariables.stream()
						.filter(cv -> cv.chatVariable.name().equals(pathVariableName))
						.findFirst()
						.orElse(ANY_CONTENT);
					return out;
				} else {
					String trimmedWord = word.trim();
					return new Word() {

						@Override
						public String getText() {
							return trimmedWord;
						}
					
						@Override
						public String getIdentifier() {
							return trimmedWord;
						}
						
					};
				}
			})
			.collect(Collectors.toList());
		
		return Message.of(items);
	}
	
	private List<WildcardContent> createWildcardContent(Exposed mapping, HandlerMethod method) {
		MethodParameter[] params = method.getMethodParameters();
		return Arrays.stream(params)
				.filter(m -> m.getParameterAnnotation(ChatVariable.class) != null)
				.map(m -> {
					ChatVariable cv = m.getParameterAnnotation(ChatVariable.class);
					Type t = m.getGenericParameterType();
					return new WildcardContent(cv, t, Arity.ONE);
				})
				.collect(Collectors.toList());
	}

	@Override
	protected MappingRegistration<Exposed> createMappingRegistration(Exposed mapping, HandlerMethod handlerMethod) {
	
		List<WildcardContent> wildcards = createWildcardContent(mapping, handlerMethod);
		List<MessageMatcher> matchers = createMessageMatchers(mapping, wildcards); 
		
		
		return new MappingRegistration<Exposed>(mapping, handlerMethod) {
			
			@Override
			public HandlerExecutor matches(Action a) {
				if (a instanceof ElementsAction) {
					return matchesElementsAction((ElementsAction)a);
				}
				
				if (a instanceof SimpleMessageAction) {
					return matchesSimpleMessageAction((SimpleMessageAction)a);
				}
				
				return null;
			}

			private HandlerExecutor matchesSimpleMessageAction(SimpleMessageAction a) {
				Exposed e = getMapping();
				
				if (!e.isMessage()) {
					return null;
				}
				
				return pathMatches(a.getWords());
			}

			private HandlerExecutor pathMatches(Message words) {
				for (MessageMatcher messageMatcher : matchers) {
					Map<ChatVariable, Content> map = new HashMap<>();
					if (messageMatcher.consume(words, map)) {
						return new HandlerExecutor() {

							@Override
							public Map<ChatVariable, Content> getReplacements() {
								return map;
							}

							@Override
							public HandlerMethod method() {
								return handlerMethod;
							}
							
							
						};
					}
				}
				
				return null;
			}

			private HandlerExecutor matchesElementsAction(ElementsAction a) {
				Exposed e = getMapping();
				
				if (!e.isButton()) {
					return null;
				}
				
				if (e.formClass() != null) {
					Class<?> expectedFormClass = e.formClass();
					if (!expectedFormClass.isAssignableFrom(a.getFormData().getClass())) {
						return null;
					}
				}
				
				return null;  // fix this later.
			}
		};
	}


}
