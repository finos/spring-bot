package org.finos.symphony.toolkit.workflow.java.mapping;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.finos.symphony.toolkit.workflow.actions.Action;
import org.finos.symphony.toolkit.workflow.actions.SimpleMessageAction;
import org.finos.symphony.toolkit.workflow.annotations.ChatRequest;
import org.finos.symphony.toolkit.workflow.annotations.ChatVariable;
import org.finos.symphony.toolkit.workflow.annotations.WorkMode;
import org.finos.symphony.toolkit.workflow.content.Addressable;
import org.finos.symphony.toolkit.workflow.content.Content;
import org.finos.symphony.toolkit.workflow.content.Message;
import org.finos.symphony.toolkit.workflow.content.User;
import org.finos.symphony.toolkit.workflow.content.Word;
import org.finos.symphony.toolkit.workflow.java.converters.ResponseConverter;
import org.finos.symphony.toolkit.workflow.java.mapping.WildcardContent.Arity;
import org.finos.symphony.toolkit.workflow.java.resolvers.WorkflowResolversFactory;
import org.finos.symphony.toolkit.workflow.response.handlers.ResponseHandlers;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotatedElementUtils;

public class ChatRequestChatHandlerMapping extends AbstractSpringComponentHandlerMapping<ChatRequest> {

	private WorkflowResolversFactory wrf;
	private ResponseHandlers rh;
	private List<ResponseConverter> converters;
	
	public ChatRequestChatHandlerMapping(WorkflowResolversFactory wrf, ResponseHandlers rh, List<ResponseConverter> converters) {
		super();
		this.wrf = wrf;
		this.rh = rh;
		this.converters = converters;
	}

	@Override
	protected ChatRequest getMappingForMethod(Method method, Class<?> handlerType) {
		if (AnnotatedElementUtils.hasAnnotation(method, ChatRequest.class)) {
			return AnnotatedElementUtils.getMergedAnnotation(method, ChatRequest.class);
		} else {
			return null;
		}
	}

	@Override
	public List<ChatMapping<ChatRequest>> getHandlers(Action a) {
		List<ChatMapping<ChatRequest>> out = getAllHandlers(a.getAddressable(), a.getUser()).stream()
				.filter(m -> m.getExecutor(a) != null)
				.collect(Collectors.toList());

		return out;
	}
	
	@Override
	public List<ChatMapping<ChatRequest>> getAllHandlers(Addressable a, User u) {
		mappingRegistry.acquireReadLock();

		List<ChatMapping<ChatRequest>> out = mappingRegistry.getRegistrations().values().stream()
				.filter(cm -> canBePerformedHere(cm, a, u))
				.collect(Collectors.toList());

		mappingRegistry.releaseReadLock();
		return out;
	}

	private boolean canBePerformedHere(MappingRegistration<ChatRequest> cm, Addressable a, User u) {
		return true;
		//TODO: write this
	}

	@Override
	public List<ChatHandlerExecutor> getExecutors(Action a) {
		List<ChatHandlerExecutor> out = getAllHandlers(a.getAddressable(), a.getUser()).stream()
				.map(m -> m.getExecutor(a))
				.filter(f -> f != null)
				.collect(Collectors.toList());

		return out;
	}

	protected List<MessageMatcher> createMessageMatchers(ChatRequest mapping, List<WildcardContent> chatVariables) {
		List<MessageMatcher> parts = Arrays.stream(mapping.value()).map(str -> createContentPattern(str, chatVariables))
				.map(cp -> new MessageMatcher(cp)).collect(Collectors.toList());

		return parts;
	}

	private static WildcardContent ANY_CONTENT = new WildcardContent(null, Content.class, Arity.ONE);

	private Content createContentPattern(String str, List<WildcardContent> chatVariables) {
		List<Content> items = Arrays.stream(str.split("\\s")).map(word -> {
			if (word.startsWith("{") && word.endsWith("}")) {
				String pathVariableName = word.substring(1, word.length() - 1);
				WildcardContent out = chatVariables.stream()
						.filter(cv -> cv.chatVariable.name().equals(pathVariableName)).findFirst().orElse(ANY_CONTENT);
				return out;
			} else {
				String trimmedWord = word.trim();
				return new Word() {

					@Override
					public String getText() {
						return trimmedWord;
					}

					@Override
					public String toString() {
						return "Word [" + trimmedWord + "]";
					}

				};
			}
		}).collect(Collectors.toList());

		return Message.of(items);
	}

	private List<WildcardContent> createWildcardContent(ChatRequest mapping, ChatHandlerMethod method) {
		MethodParameter[] params = method.getMethodParameters();
		return Arrays.stream(params).filter(m -> m.getParameterAnnotation(ChatVariable.class) != null).map(m -> {
			ChatVariable cv = m.getParameterAnnotation(ChatVariable.class);
			Type t = m.getGenericParameterType();
			return new WildcardContent(cv, t, Arity.ONE);
		}).collect(Collectors.toList());
	}

	@Override
	protected MappingRegistration<ChatRequest> createMappingRegistration(ChatRequest mapping, ChatHandlerMethod handlerMethod) {
	
		List<WildcardContent> wildcards = createWildcardContent(mapping, handlerMethod);
		List<MessageMatcher> matchers = createMessageMatchers(mapping, wildcards); 
		
		return new MappingRegistration<ChatRequest>(mapping, handlerMethod) {
			
			@Override
			public ChatHandlerExecutor getExecutor(Action a) {
				
				if (a instanceof SimpleMessageAction) {
					return matchesSimpleMessageAction((SimpleMessageAction)a);
				}
				
				return null;
			}

			private ChatHandlerExecutor matchesSimpleMessageAction(SimpleMessageAction a) {
				return pathMatches(a.getWords(), a);
			}

			private ChatHandlerExecutor pathMatches(Message words, Action a) {
				MappingRegistration<?> me = this;
				ChatHandlerExecutor bestMatch = null;
				
				for (MessageMatcher messageMatcher : matchers) {
					Map<ChatVariable, Content> map = new HashMap<>();
					
					if (messageMatcher.consume(words, map)) {
						if ((bestMatch == null) || (bestMatch.getReplacements().size() < map.size())) {
							bestMatch = new AbstractHandlerExecutor(wrf, rh, converters) {
	
								@Override
								public Map<ChatVariable, Content> getReplacements() {
									return map;
								}
	
								@Override
								public Action action() {
									return a;
								}

								@Override
								public ChatMapping<?> getOriginatingMapping() {
									return me;
								}
	
							};
						}
					}
					
				}
					
				return bestMatch;
			}

			@Override
			public boolean isButtonFor(Object o, WorkMode m) {
				return false;
			}
		};
	}

}