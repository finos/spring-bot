package com.github.deutschebank.symphony.workflow.sources.symphony.messages;

import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Optional;

import com.github.deutschebank.symphony.workflow.Action;
import com.github.deutschebank.symphony.workflow.content.Addressable;
import com.github.deutschebank.symphony.workflow.content.CashTag;
import com.github.deutschebank.symphony.workflow.content.Content;
import com.github.deutschebank.symphony.workflow.content.HashTag;
import com.github.deutschebank.symphony.workflow.content.Message;
import com.github.deutschebank.symphony.workflow.content.Paragraph;
import com.github.deutschebank.symphony.workflow.content.PastedTable;
import com.github.deutschebank.symphony.workflow.content.Tag;
import com.github.deutschebank.symphony.workflow.content.User;
import com.github.deutschebank.symphony.workflow.content.Word;
import com.github.deutschebank.symphony.workflow.java.resolvers.WorkflowResolver;
import com.github.deutschebank.symphony.workflow.java.resolvers.WorkflowResolverFactory;

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
				public Optional<Object> resolve(Class<?> cl, Addressable a) {
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
				public Optional<Object> resolve(Class<?> c, Addressable a) {
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
