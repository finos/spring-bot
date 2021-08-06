package org.finos.symphony.toolkit.workflow.java.mapping;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.finos.symphony.toolkit.workflow.actions.Action;
import org.finos.symphony.toolkit.workflow.actions.FormAction;
import org.finos.symphony.toolkit.workflow.annotations.ButtonRequest;
import org.finos.symphony.toolkit.workflow.annotations.ChatVariable;
import org.finos.symphony.toolkit.workflow.annotations.WorkMode;
import org.finos.symphony.toolkit.workflow.content.Addressable;
import org.finos.symphony.toolkit.workflow.content.User;
import org.finos.symphony.toolkit.workflow.java.converters.ResponseConverter;
import org.finos.symphony.toolkit.workflow.java.resolvers.WorkflowResolversFactory;
import org.finos.symphony.toolkit.workflow.response.handlers.ResponseHandlers;
import org.springframework.core.annotation.AnnotatedElementUtils;

public class ButtonRequestChatHandlerMapping extends AbstractSpringComponentHandlerMapping<ButtonRequest> {

	private WorkflowResolversFactory wrf;
	private ResponseHandlers rh;
	private List<ResponseConverter> converters;
	
	public ButtonRequestChatHandlerMapping(WorkflowResolversFactory wrf, ResponseHandlers rh, List<ResponseConverter> converters) {
		super();
		this.wrf = wrf;
		this.rh = rh;
		this.converters = converters;
	}

	@Override
	protected ButtonRequest getMappingForMethod(Method method, Class<?> handlerType) {
		if (AnnotatedElementUtils.hasAnnotation(method, ButtonRequest.class)) {
			return AnnotatedElementUtils.getMergedAnnotation(method, ButtonRequest.class);
		} else {
			return null;
		}
	}

	@Override
	public List<ChatMapping<ButtonRequest>> getHandlers(Action a) {
		List<ChatMapping<ButtonRequest>> out = getAllHandlers(a.getAddressable(), a.getUser()).stream()
				.filter(m -> m.getExecutor(a) != null)
				.collect(Collectors.toList());

		return out;
	}
	
	@Override
	public List<ChatMapping<ButtonRequest>> getAllHandlers(Addressable a, User u) {
		mappingRegistry.acquireReadLock();

		List<ChatMapping<ButtonRequest>> out = mappingRegistry.getRegistrations().values().stream()
				.filter(cm -> canBePerformedHere(cm, a, u))
				.collect(Collectors.toList());

		mappingRegistry.releaseReadLock();
		return out;
	}

	private boolean canBePerformedHere(MappingRegistration<ButtonRequest> cm, Addressable a, User u) {
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

	@Override
	protected MappingRegistration<ButtonRequest> createMappingRegistration(ButtonRequest mapping, ChatHandlerMethod handlerMethod) {
			
		return new MappingRegistration<ButtonRequest>(mapping, handlerMethod) {
			
			@Override
			public ChatHandlerExecutor getExecutor(Action a) {
				if (a instanceof FormAction) {
					return matchesFormAction((FormAction)a);
				}
				
				return null;
			}

			private ChatHandlerExecutor matchesFormAction(FormAction a) {
				MappingRegistration<?> me = this;
				ButtonRequest e = getMapping();
				
				
				Class<?> expectedFormClass = e.value();
				if (!expectedFormClass.isAssignableFrom(a.getFormData().getClass())) {
					return null;
				}
					
				if (!a.getAction().equals(this.getUniqueName())) {
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
				return mapping.value().isAssignableFrom(o.getClass());
			}
		};
	}

}
