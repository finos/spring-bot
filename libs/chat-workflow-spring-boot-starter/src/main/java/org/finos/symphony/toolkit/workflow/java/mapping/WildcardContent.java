package org.finos.symphony.toolkit.workflow.java.mapping;

import java.lang.reflect.Type;

import org.finos.symphony.toolkit.workflow.annotations.ChatVariable;
import org.finos.symphony.toolkit.workflow.content.Content;

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
	Type expected;
	Arity arity;

	@Override
	public String getText() {
		return "{"+chatVariable.name()+"}";
	}

}
