package org.finos.symphony.toolkit.workflow.java.resolvers;

import java.util.Optional;

import org.finos.symphony.toolkit.workflow.annotations.Exposed;
import org.finos.symphony.toolkit.workflow.content.Addressable;
import org.finos.symphony.toolkit.workflow.content.Room;
import org.finos.symphony.toolkit.workflow.content.User;
import org.finos.symphony.toolkit.workflow.java.mapping.ChatHandlerExecutor;
import org.springframework.core.MethodParameter;

/** 
 * Resolves subclasses of {@link Addressable} when used as parameters of {@link Exposed}
 * methods.
 * 
 * @author rob@kite9.com
 *
 */
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
				} else if (User.class.isAssignableFrom(cl)) {
					return Optional.of(che.action().getUser());
				} else if (Addressable.class.isAssignableFrom(cl)) {
					Addressable a = che.action().getAddressable();
					return Optional.of(a);
				}
				
				return Optional.empty();
			}
			
			@Override
			public boolean canResolve(MethodParameter mp) {
				Class<?> cl = mp.getParameterType();
				return (Addressable.class.isAssignableFrom(cl));
			}
		};
	}

	@Override
	public int getOrder() {
		return NORMAL_PRIORITY;
	}

}
