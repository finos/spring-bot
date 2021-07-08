package org.finos.symphony.toolkit.workflow.sources.symphony.messages;

import java.util.List;
import java.util.function.Function;

import org.finos.symphony.toolkit.workflow.response.Response;

/**
 * Deals with simple text-commands coming from users, to start workflows etc.  
 * 
 * @author Rob Moffat
 * 
 * First argument of apply is the stream id, the next is the words.
 *
 */
public interface SimpleMessageConsumer extends Function<SimpleMessageAction, List<Response>> {
	
	public default boolean requiresAddressing() {
		return true;
	}

}
