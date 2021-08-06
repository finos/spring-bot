package org.finos.symphony.toolkit.workflow.java.resolvers;

import java.lang.reflect.Type;
import java.util.Optional;

import org.finos.symphony.toolkit.workflow.annotations.Work;
import org.finos.symphony.toolkit.workflow.history.History;
import org.finos.symphony.toolkit.workflow.java.mapping.ChatHandlerExecutor;
import org.springframework.core.MethodParameter;

public class MessageHistoryWorkflowResolverFactory implements WorkflowResolverFactory {
	
	private final class MessageHistoryWorkflowResolver implements WorkflowResolver {
		private final ChatHandlerExecutor che;

		private MessageHistoryWorkflowResolver(ChatHandlerExecutor che) {
			this.che = che;
		}

		@SuppressWarnings("unchecked")
		@Override
		public Optional<Object> resolve(MethodParameter mp) {
			Type t = mp.getGenericParameterType();
			
			if (t instanceof Class<?>) {
				return (Optional<Object>) hist.getLastFromHistory((Class<?>) t, che.action().getAddressable());
			}
			
			return Optional.empty();
			
		}

		@Override
		public boolean canResolve(MethodParameter mo) {
			Type t = mo.getGenericParameterType();
			
			if (t instanceof Class<?>) {
				Work w = ((Class<?>)t).getAnnotation(Work.class);
				if (w != null) {
					return true;
				}					
			}
			
			return false;
		}
	}

	History hist;
	
	public MessageHistoryWorkflowResolverFactory(History hist) {
		this.hist = hist;
	}

	@Override
	public int getOrder() {
		return WorkflowResolverFactory.LOW_PRIORITY;
	}

	@Override
	public WorkflowResolver createResolver(ChatHandlerExecutor che) {
		return new MessageHistoryWorkflowResolver(che);
	}

}
