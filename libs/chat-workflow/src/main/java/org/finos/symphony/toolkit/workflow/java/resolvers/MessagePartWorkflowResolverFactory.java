package org.finos.symphony.toolkit.workflow.java.resolvers;

import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.finos.symphony.toolkit.workflow.actions.Action;
import org.finos.symphony.toolkit.workflow.actions.SimpleMessageAction;
import org.finos.symphony.toolkit.workflow.content.Content;
import org.finos.symphony.toolkit.workflow.content.Message;
import org.finos.symphony.toolkit.workflow.java.mapping.ChatHandlerExecutor;

/**
 * Provides a resolver for parts of a message, e.g. words, paragraphs, tags.
 * 
 * @author moffrob
 *
 */
public class MessagePartWorkflowResolverFactory implements WorkflowResolverFactory {
	
	
	private final class ContentWorkflowResolver extends AbstractClassWorkflowResolver {
		private final Map<Class<?>, Deque<Object>> parameterBuckets;

		private ContentWorkflowResolver(Map<Class<?>, Deque<Object>> parameterBuckets) {
			this.parameterBuckets = parameterBuckets;
		}

		@Override
		public Optional<Object> resolve(Class<?> t) {
			if (parameterBuckets.containsKey(t)) {
				return Optional.of(parameterBuckets.get(t).pop());
			} else {
				return Optional.empty();
			}
		}

		@Override
		public boolean canResolve(Class<?> t) {
			return parameterBuckets.containsKey(t);
		}

	}


	private List<Class<? extends Content>> contentClasses;
	

	public MessagePartWorkflowResolverFactory(List<Class<? extends Content>> contentClasses) {
		super();
		this.contentClasses = contentClasses;
	}


	@Override
	public int getOrder() {
		return LOW_PRIORITY;
	}


	@Override
	public WorkflowResolver createResolver(ChatHandlerExecutor che) {
		Action a = che.action();
		
		if (a instanceof SimpleMessageAction) {
			final Map<Class<?>, Deque<Object>> parameterBuckets = setupParameterBuckets(((SimpleMessageAction) a).getMessage());
			return new ContentWorkflowResolver(parameterBuckets);
		} else {
			return new NullWorkflowResolver();
		}
	}

	
	protected Map<Class<?>, Deque<Object>> setupParameterBuckets(Message m) {
		Map<Class<?>, Deque<Object>> out = new HashMap<>();
		for (Class<? extends Content> class1 : contentClasses) {
			Deque<Object> l = new LinkedList<Object>();
			l.addAll(m.only(class1));
			out.put(class1, l);
		}
		
		return out;
	}

}
