package org.finos.springbot.workflow.java.converters;

import java.util.function.BiConsumer;

import org.finos.springbot.workflow.java.mapping.ChatHandlerExecutor;
import org.springframework.core.PriorityOrdered;

/**
 * Converts the output of a method call into Response objects, sends to the ResponseHandlers.
 * 
 * @author rob@kite9.com
 *
 */
public interface ResponseConverter extends PriorityOrdered, BiConsumer<Object, ChatHandlerExecutor> {

}
