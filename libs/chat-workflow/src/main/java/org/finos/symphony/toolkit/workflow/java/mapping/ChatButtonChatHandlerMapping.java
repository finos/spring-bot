package org.finos.symphony.toolkit.workflow.java.mapping;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.finos.symphony.toolkit.workflow.actions.Action;
import org.finos.symphony.toolkit.workflow.actions.FormAction;
import org.finos.symphony.toolkit.workflow.annotations.ChatButton;
import org.finos.symphony.toolkit.workflow.annotations.ChatVariable;
import org.finos.symphony.toolkit.workflow.annotations.WorkMode;
import org.finos.symphony.toolkit.workflow.content.Addressable;
import org.finos.symphony.toolkit.workflow.content.Chat;
import org.finos.symphony.toolkit.workflow.content.User;
import org.finos.symphony.toolkit.workflow.conversations.Conversations;
import org.finos.symphony.toolkit.workflow.help.HelpPage;
import org.finos.symphony.toolkit.workflow.java.converters.ResponseConverter;
import org.finos.symphony.toolkit.workflow.java.resolvers.WorkflowResolversFactory;
import org.finos.symphony.toolkit.workflow.response.handlers.ResponseHandlers;
import org.springframework.core.annotation.AnnotatedElementUtils;

public class ChatButtonChatHandlerMapping extends AbstractSpringComponentHandlerMapping<ChatButton> {

	private WorkflowResolversFactory wrf;
	private ResponseHandlers rh;
	private List<ResponseConverter> converters;
	private Conversations conversations;
	
	public ChatButtonChatHandlerMapping(WorkflowResolversFactory wrf, ResponseHandlers rh, List<ResponseConverter> converters, Conversations conversations) {
		super();
		this.wrf = wrf;
		this.rh = rh;
		this.converters = converters;
		this.conversations = conversations;
	}

	@Override
	protected ChatButton getMappingForMethod(Method method, Class<?> handlerType) {
		if (AnnotatedElementUtils.hasAnnotation(method, ChatButton.class)) {
			return AnnotatedElementUtils.getMergedAnnotation(method, ChatButton.class);
		} else {
			return null;
		}
	}

	@Override
	public List<ChatMapping<ChatButton>> getHandlers(Action a) {
		List<ChatMapping<ChatButton>> out = getAllHandlers(a.getAddressable(), a.getUser()).stream()
				.filter(m -> m.getExecutor(a) != null)
				.collect(Collectors.toList());

		return out;
	}
	
	@Override
	public List<ChatMapping<ChatButton>> getAllHandlers(Addressable a, User u) {
		mappingRegistry.acquireReadLock();

		List<ChatMapping<ChatButton>> out = new ArrayList<>(mappingRegistry.getRegistrations().values());

		mappingRegistry.releaseReadLock();
		out = out.stream().filter(r -> canBePerformed(a, u, r.getMapping())).collect(Collectors.toList());
		return out;
	}

	@Override
	public List<ChatHandlerExecutor> getExecutors(Action a) {
		List<ChatHandlerExecutor> out = getAllHandlers(a.getAddressable(), a.getUser()).stream()
				.map(m -> m.getExecutor(a))
				.filter(f -> f != null)
				.collect(Collectors.toList());

		return out;
	}
	
	private boolean canBePerformed(Addressable a, User u, ChatButton cb) {
		
		if ((a instanceof Chat) && (cb.excludeRooms().length > 0)) {
			if (roomMatched(cb.excludeRooms(), (Chat) a)) {
				return false;
			}
		}
		
		if ((a instanceof Chat) && (cb.rooms().length > 0)) {
			if (!roomMatched(cb.rooms(), (Chat) a)) {
				return false;
			}
		}
		
		if (Objects.nonNull(u) && cb.admin() && (a instanceof Chat)) {
			List<User> chatAdmins = conversations.getChatAdmins((Chat) a);
			return chatAdmins.contains(u);
		} else {
			return true;
		}
	}

	@Override
	protected MappingRegistration<ChatButton> createMappingRegistration(ChatButton mapping, ChatHandlerMethod handlerMethod) {
			
		return new MappingRegistration<ChatButton>(mapping, handlerMethod) {
			
			@Override
			public ChatHandlerExecutor getExecutor(Action a) {
				if (a instanceof FormAction) {
					return matchesFormAction((FormAction)a);
				}
				
				return null;
			}

			private boolean canBePerformedHere(FormAction a) {
				ChatButton cb = getMapping();
			
				return canBePerformed(a.getAddressable(), a.getUser(), cb);
			}

			private ChatHandlerExecutor matchesFormAction(FormAction a) {
				MappingRegistration<?> me = this;
				
				if(Objects.nonNull(a.getData().get("form")) && a.getData().get("form").getClass() == HelpPage.class) {
					return null;
				}
					
				if (!a.getAction().equals(this.getUniqueName())) {
					return null;
				}
				
				if (!canBePerformedHere(a)) {
					return null;
				}
				
				return new AbstractHandlerExecutor(wrf, rh, converters) {
					
					@Override
					public Map<ChatVariable, Object> getReplacements() {
						return Collections.emptyMap();
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

			@Override
			public boolean isButtonFor(Object o, WorkMode m) {
				return mapping.value().isAssignableFrom(o.getClass()) && workModeMatches(m, this.getMapping().showWhen());
					
			}

			private boolean workModeMatches(WorkMode ourMode, WorkMode buttonMode) {
				if (buttonMode == WorkMode.BOTH) {
					return true;
				} else {
					return buttonMode == ourMode;
				}
			}
		};
	}

}
