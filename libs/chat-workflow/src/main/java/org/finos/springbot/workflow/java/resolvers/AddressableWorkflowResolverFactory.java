package org.finos.springbot.workflow.java.resolvers;

import java.util.Optional;

import org.finos.springbot.workflow.annotations.ChatRequest;
import org.finos.springbot.workflow.content.Addressable;
import org.finos.springbot.workflow.content.Chat;
import org.finos.springbot.workflow.content.User;
import org.finos.springbot.workflow.java.mapping.ChatHandlerExecutor;

/** 
 * Resolves subclasses of {@link Addressable} when used as parameters of {@link ChatRequest}
 * methods.
 * 
 * @author rob@kite9.com
 *
 */
public class AddressableWorkflowResolverFactory implements WorkflowResolverFactory {
	
	private final class AddressableWorkflowResolver extends AbstractClassWorkflowResolver {
		private final ChatHandlerExecutor che;

		private AddressableWorkflowResolver(ChatHandlerExecutor che) {
			this.che = che;
		}

		@Override
		public Optional<Object> resolve(Class<?> cl) {
			if (Chat.class.isAssignableFrom(cl)) {
				Addressable a = che.action().getAddressable();
				if (a instanceof Chat) {
					return Optional.of((Chat) a);
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
		public boolean canResolve(Class<?> cl) {
			return (Addressable.class.isAssignableFrom(cl));
		}
	}

	@Override
	public WorkflowResolver createResolver(ChatHandlerExecutor che) {
		
		return new AddressableWorkflowResolver(che);
	}

	@Override
	public int getOrder() {
		return NORMAL_PRIORITY;
	}

}
