package org.finos.symphony.toolkit.workflow.java.resolvers;

import java.util.Optional;

import org.finos.symphony.toolkit.workflow.annotations.ChatVariable;
import org.finos.symphony.toolkit.workflow.java.mapping.ChatHandlerExecutor;
import org.springframework.core.MethodParameter;

public class ChatVariableWorkflowResolverFactory implements WorkflowResolverFactory {
	

	private final class ChatVariableWorkflowResolver implements WorkflowResolver {
		private final ChatHandlerExecutor che;

		private ChatVariableWorkflowResolver(ChatHandlerExecutor che) {
			this.che = che;
		}

		@Override
		public Optional<Object> resolve(MethodParameter mp) {
			ChatVariable cv = mp.getParameterAnnotation(ChatVariable.class);
			if (cv != null) {
				Object out = che.getReplacements().get(cv);
				if (out == null) {
					return Optional.empty();
				} else {
					return Optional.of(out);
				}
			}
			
			return Optional.empty();
		}

		@Override
		public boolean canResolve(MethodParameter mp) {
			ChatVariable cv = mp.getParameterAnnotation(ChatVariable.class);
			return cv != null;
		}
	}

	@Override
	public WorkflowResolver createResolver(ChatHandlerExecutor che) {
		return new ChatVariableWorkflowResolver(che);
	}

}
