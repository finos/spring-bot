package org.finos.symphony.toolkit.workflow.message;

import java.lang.reflect.Type;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Optional;

import org.finos.symphony.toolkit.workflow.actions.Action;
import org.finos.symphony.toolkit.workflow.actions.SimpleMessageAction;
import org.finos.symphony.toolkit.workflow.content.CashTag;
import org.finos.symphony.toolkit.workflow.content.CodeBlock;
import org.finos.symphony.toolkit.workflow.content.Content;
import org.finos.symphony.toolkit.workflow.content.HashTag;
import org.finos.symphony.toolkit.workflow.content.Message;
import org.finos.symphony.toolkit.workflow.content.Paragraph;
import org.finos.symphony.toolkit.workflow.content.PastedTable;
import org.finos.symphony.toolkit.workflow.content.Tag;
import org.finos.symphony.toolkit.workflow.content.User;
import org.finos.symphony.toolkit.workflow.content.Word;
import org.finos.symphony.toolkit.workflow.java.mapping.ChatHandlerExecutor;
import org.finos.symphony.toolkit.workflow.java.resolvers.WorkflowResolver;
import org.finos.symphony.toolkit.workflow.java.resolvers.WorkflowResolverFactory;
import org.springframework.core.MethodParameter;

/**
 * Provides a resolver for parts of a message, e.g. words, paragraphs, tags.
 * 
 * @author moffrob
 *
 */
public class MessagePartWorkflowResolverFactory implements WorkflowResolverFactory {
	
	

	@Override
	public int getOrder() {
		return LOW_PRIORITY;
	}


	@Override
	public WorkflowResolver createResolver(ChatHandlerExecutor che) {
		Action a = che.action();
		
		if (a instanceof SimpleMessageAction) {
		
			final Map<Class<?>, Deque<Object>> parameterBuckets = setupParameterBuckets(((SimpleMessageAction) a).getWords());
			
			return new WorkflowResolver() {
				
				
				
				@Override
				public Optional<Object> resolve(MethodParameter mp) {
					Type t = mp.getGenericParameterType();
					if (parameterBuckets.containsKey(t)) {
						return Optional.of(parameterBuckets.get(t).pop());
					} else {
						return Optional.empty();
					}
				}
				
				@Override
				public boolean canResolve(MethodParameter mp) {
					Type t = mp.getGenericParameterType();
					return parameterBuckets.containsKey(t);
				}
			};
		} else {
			return new WorkflowResolver() {
				
				@Override
				public Optional<Object> resolve(MethodParameter mp) {
					return Optional.empty();
				}
				
				@Override
				public boolean canResolve(MethodParameter mp) {
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
				CashTag.class,
				CodeBlock.class};
	}
}
