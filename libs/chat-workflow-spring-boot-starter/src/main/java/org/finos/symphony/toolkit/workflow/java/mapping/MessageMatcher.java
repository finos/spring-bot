package org.finos.symphony.toolkit.workflow.java.mapping;

import java.util.Map;

import org.finos.symphony.toolkit.workflow.annotations.ChatVariable;
import org.finos.symphony.toolkit.workflow.content.Content;

public class MessageMatcher {
	
	public boolean match(Content shape, Content exact, Map<ChatVariable, Content> out) {
		
		if (shape.equals(exact)) {
			return true;
		}
		
		if (shape instanceof WildcardContent) {
			
			
			
		}
		
		if (shape instanceof Iterable<Content>) {
			
			
		}
		
	}

}
