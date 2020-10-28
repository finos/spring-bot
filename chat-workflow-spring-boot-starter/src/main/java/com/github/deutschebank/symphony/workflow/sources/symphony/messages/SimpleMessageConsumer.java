package com.github.deutschebank.symphony.workflow.sources.symphony.messages;

import java.util.List;
import java.util.function.Function;

import com.github.deutschebank.symphony.workflow.response.Response;

/**
 * Deals with simple text-commands coming from users, to start workflows etc.  
 * 
 * @author Rob Moffat
 * 
 * First argument of apply is the stream id, the next is the words.
 *
 */
public interface SimpleMessageConsumer extends Function<SimpleMessageAction, List<Response>> {

}
