package org.finos.symphony.toolkit.workflow.content;

import org.finos.symphony.toolkit.json.EntityJson;

/**
 * Parses from a messaging system to create a semantic, presentation-free Message class 
 * representing the message in a platform-agnostic way.
 * 
 * @see ContentWriter 
 * 
 * @author rob@kite9.com
 *
 */
public interface MessageParser {
	
	public Message parse(String source);

	public Message parse(String sourceFormat, EntityJson entityJson);
}
