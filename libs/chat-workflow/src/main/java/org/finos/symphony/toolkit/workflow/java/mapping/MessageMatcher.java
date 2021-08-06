package org.finos.symphony.toolkit.workflow.java.mapping;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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
	
	public boolean consume(Content message, Map<ChatVariable, Object> out) {
		return consumeCurrentPattern(pattern, message, out);
	}
	
	@SuppressWarnings("unchecked")
	private boolean consumeCurrentPattern(Content pattern, Content message, Map<ChatVariable, Object> out) {
		if ((isEmpty(pattern)) || (message.startsWith(pattern))) {
			return true;
		} else if (pattern instanceof OrderedContent) {
			OrderedContent<Content> oPattern = (OrderedContent<Content>) pattern;
			Content first = removeFirst(oPattern);
			boolean canSkip = canSkip(first);
			boolean canMulti = canMulti(first);
			boolean canNull = canNull(first);
			
			if (canMulti) {
				out.putIfAbsent(((WildcardContent)first).chatVariable, new ArrayList<Object>());
			} else if (canSkip) {
				out.putIfAbsent(((WildcardContent)first).chatVariable, Optional.empty());
			} else if (canNull) {
				out.putIfAbsent(((WildcardContent)first).chatVariable, null);
			}
			
			if (message.startsWith(first)) {
				System.out.println("Matched "+first + " with start of "+pattern);
				
				if (first instanceof WildcardContent) {
					WildcardContent wc = (WildcardContent) first;
					Class<? extends Content> contentClass = (Class<? extends Content>) wc.expected;
					Optional<? extends Content> matchingFirst = message.getNth(contentClass, 0);
					if (matchingFirst.isPresent()) {
						if (canMulti) {
							((List<Content>) out.get(wc.chatVariable)).add(matchingFirst.get());
						} else if (canSkip) {
							out.put(wc.chatVariable, matchingFirst);
						} else {
							out.put(wc.chatVariable, matchingFirst.get());	
						}
					}
				}

				message = message.removeAtStart(first);
				OrderedContent<Content> rest = canMulti ? oPattern : rest(oPattern);
				return consumeCurrentPattern(rest, message, out);
			} else if (canMulti || canSkip || canNull) {
				OrderedContent<Content> rest = rest(oPattern);
				return consumeCurrentPattern(rest, message, out);
			}
		}
		
		System.out.println("No Match "+message + " with start of "+pattern);
		return false;
	}
	
	private boolean canNull(Content first) {
		if (first instanceof WildcardContent) {
			return !((WildcardContent)first).chatVariable.required();
		} else {
			return false;
		}
	}
	
	private boolean canSkip(Content first) {
		if (first instanceof WildcardContent) {
			switch (((WildcardContent) first).arity) {
			case LIST:
				return true;
			case OPTIONAL:
				return true;
			default: 
			case ONE:
				return false;
			}
			
		} else {
			return false;
		}
	}
	
	private boolean canMulti(Content first) {
		if (first instanceof WildcardContent) {
			switch (((WildcardContent) first).arity) {
			case LIST:
				return true;
			case OPTIONAL:
				return false;
			default: 
			case ONE:
				return false;
			}
			
		} else {
			return false;
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
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
