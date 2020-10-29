package org.finos.symphony.toolkit.workflow.sources.symphony.messages;

import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Optional;

import org.finos.symphony.toolkit.workflow.Action;
import org.finos.symphony.toolkit.workflow.content.Addressable;
import org.finos.symphony.toolkit.workflow.content.CashTag;
import org.finos.symphony.toolkit.workflow.content.Content;
import org.finos.symphony.toolkit.workflow.content.HashTag;
import org.finos.symphony.toolkit.workflow.content.Message;
import org.finos.symphony.toolkit.workflow.content.Paragraph;
import org.finos.symphony.toolkit.workflow.content.PastedTable;
import org.finos.symphony.toolkit.workflow.content.Tag;
import org.finos.symphony.toolkit.workflow.content.User;
import org.finos.symphony.toolkit.workflow.content.Word;
import org.finos.symphony.toolkit.workflow.java.resolvers.WorkflowResolver;
import org.finos.symphony.toolkit.workflow.java.resolvers.WorkflowResolverFactory;

/**
 * Provides a resolver for parts of a message, e.g. words, paragraphs, tags.
 * 
 * @author moffrob
 *
 */
public class MessagePartWorkflowResolverFactory implements WorkflowResolverFactory {

	@Override
	public WorkflowResolver createResolver(Action a) {
		
		if (a instanceof SimpleMessageAction) {
		
			final Map<Class<?>, Deque<Object>> parameterBuckets = setupParameterBuckets(((SimpleMessageAction) a).getWords());
			
			return new WorkflowResolver() {
				
				@Override
				public Optional<Object> resolve(Class<?> cl, Addressable a, boolean isTarget) {
					if (isTarget) {
						return Optional.empty();
					}
					
					if (parameterBuckets.containsKey(cl)) {
						return Optional.of(parameterBuckets.get(cl).pop());
					} else {
						return Optional.empty();
					}
				}
				
				@Override
				public boolean canResolve(Class<?> cl) {
					return parameterBuckets.containsKey(cl);
				}
			};
		} else {
			return new WorkflowResolver() {
				
				@Override
				public Optional<Object> resolve(Class<?> c, Addressable a, boolean isTarget) {
					return Optional.empty();
				}
				
				@Override
				public boolean canResolve(Class<?> c) {
					return false;
				}
			};
		}
	}

	
	protected Map<Class<?>, Deque<Object>> setupParameterBuckets(Message m) {
		Map<Class<?>, Deque<Object>> out = new HashMap<>();
		for (Class<? extends Content> class1 : getResolvableClasses()) {
			Deque<Object> l = new LinkedList<Object>();
			l.addAll(m.only(class1));
			out.put(class1, l);
		}
		
		return out;
	}


	@SuppressWarnings("unchecked")
	protected Class<? extends Content>[] getResolvableClasses() {
		return new Class[] { Message.class, 
				Paragraph.class, 
				PastedTable.class, 
				Word.class, 
				Tag.class, 
				User.class, 
				HashTag.class, 
				CashTag.class};
	}
}
