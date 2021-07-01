package org.finos.symphony.toolkit.workflow.java.resolvers;

import java.util.Optional;

import org.finos.symphony.toolkit.workflow.annotations.ChatVariable;
import org.finos.symphony.toolkit.workflow.content.Content;
import org.finos.symphony.toolkit.workflow.java.mapping.ChatHandlerExecutor;
import org.springframework.core.MethodParameter;

public class ChatVariableWorkflowResolverFactory implements WorkflowResolverFactory {
	

	@Override
	public WorkflowResolver createResolver(ChatHandlerExecutor che) {
		return new WorkflowResolver() {
			
			@Override
			public Optional<Object> resolve(MethodParameter mp) {
				ChatVariable cv = mp.getParameterAnnotation(ChatVariable.class);
				if (cv != null) {
					Content out = che.getReplacements().get(cv);
					return Optional.of(out);
				}
				
				return Optional.empty();
			}
			
			@Override
			public boolean canResolve(MethodParameter mp) {
				return mp.getMethodAnnotation(ChatVariable.class) != null;
			}
		};
	}

}
