package org.finos.symphony.toolkit.workflow.java.resolvers;

import java.util.Optional;

import org.finos.symphony.toolkit.workflow.content.Addressable;
import org.finos.symphony.toolkit.workflow.content.Author;
import org.finos.symphony.toolkit.workflow.content.Room;
import org.finos.symphony.toolkit.workflow.java.mapping.ChatHandlerExecutor;
import org.springframework.core.MethodParameter;

public class AddressableWorkflowResolverFactory implements WorkflowResolverFactory {
	
	@Override
	public WorkflowResolver createResolver(ChatHandlerExecutor che) {
		
		return new WorkflowResolver() {
			
			@Override
			public Optional<Object> resolve(MethodParameter mp) {
				Class<?> cl = mp.getParameterType();
				if (Room.class.isAssignableFrom(cl)) {
					Addressable a = che.action().getAddressable();
					if (a instanceof Room) {
						return Optional.of((Room) a);
					}
				} else if (Addressable.class.isAssignableFrom(cl)) {
					Addressable a = che.action().getAddressable();
					return Optional.of(a);
				} else if (Author.class.isAssignableFrom(cl)) {
					return Optional.of((Author) che.action().getUser());
				}
				
				return Optional.empty();
			}
			
			@Override
			public boolean canResolve(MethodParameter mp) {
				Class<?> cl = mp.getParameterType();
				if (Room.class.isAssignableFrom(cl)) {
					return true;
				} else if (Addressable.class.isAssignableFrom(cl)) {
					return true;
				} else if (Author.class.isAssignableFrom(cl)) {
					return true;
				} else {
					return false;
				}
			}
		};
	}

	@Override
	public int priority() {
		return LOW_PRIORITY;
	}

}
