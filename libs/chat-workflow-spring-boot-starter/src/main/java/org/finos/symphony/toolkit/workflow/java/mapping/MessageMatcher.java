package org.finos.symphony.toolkit.workflow.java.mapping;

import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

import org.finos.symphony.toolkit.workflow.annotations.ChatVariable;
import org.finos.symphony.toolkit.workflow.content.Content;
import org.finos.symphony.toolkit.workflow.content.OrderedContent;

public class MessageMatcher {
	
	private Content pattern;
	
	public MessageMatcher(Content pattern) {
		this.pattern = pattern;
	}
	
	public boolean consume(Content message, Map<ChatVariable, Content> out) {
		return consumeCurrentPattern(pattern, message, out);
	}
	
	private boolean consumeCurrentPattern(Content pattern, Content message, Map<ChatVariable, Content> out) {
		if ((isEmpty(pattern)) || (message.startsWith(pattern))) {
			return true;
		} else if (pattern instanceof OrderedContent) {
			Content first = removeFirst((OrderedContent<?>) pattern);
			@SuppressWarnings("unchecked")
			OrderedContent<Content> rest = rest((OrderedContent<Content>) pattern);
			if (message.startsWith(first)) {
				
				if (first instanceof WildcardContent) {
					WildcardContent wc = (WildcardContent) first;
					Class<? extends Content> contentClass = (Class<? extends Content>) wc.expected;
					Optional<? extends Content> matchingFirst = message.getNth(contentClass, 0);
					if (matchingFirst.isPresent()) {
						out.put(wc.chatVariable, matchingFirst.get());
					}
				}

				message = message.removeAtStart(first);
				return consumeCurrentPattern(rest, message, out);
			} 	
			
			
		}
		
		return false;
	}
	
	

	private boolean isEmpty(Content p) {
		if (p instanceof OrderedContent) {
			for (Iterator<Content>	 iterator = ((OrderedContent)p).iterator(); iterator.hasNext();) {
				Content c = iterator.next();
				if (!isEmpty(c)) {
					return false;
				}
			}
			
			return true;
		} else {
			return p==null;
		}
	}

	private <X extends Content> X removeFirst(OrderedContent<X> c1) {
		if (c1.size() > 0) {
			return c1.getContents().get(0);
		} else {
			return null;
		}
	}
	
	private <X extends Content>  OrderedContent<X> rest(OrderedContent<X> c1) {
		if (c1.size() > 0) {
			return c1.buildAnother(c1.getContents().subList(1, c1.size()));
		} else {
			return null;
		}
	}

}
