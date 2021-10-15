package org.finos.springbot.workflow.java.mapping;

import org.finos.springbot.workflow.annotations.ChatVariable;
import org.finos.springbot.workflow.content.Content;

/**
 * This allows portions of the user's text to match 
 * 
 * @author rob@kite9.com
 *
 */
public class WildcardContent implements Content {
	
	enum Arity { ONE, OPTIONAL, LIST }
	
	public WildcardContent(ChatVariable chatVariable, Class<? extends Content> expected, Arity a) {
		super();
		this.chatVariable = chatVariable;
		this.expected = expected;
		this.arity = a;
	}

	ChatVariable chatVariable;
	Class<? extends Content> expected;
	Arity arity;

	@Override
	public String getText() {
		return "{"+chatVariable.name()+"}";
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public boolean matches(Content other) {
		return ((Class)expected).isAssignableFrom(other.getClass());
	}

	@Override
	public String toString() {
		return "WildcardContent ["+chatVariable+", "+expected.getTypeName()+", "+arity+"]";
	}

	
	
}
