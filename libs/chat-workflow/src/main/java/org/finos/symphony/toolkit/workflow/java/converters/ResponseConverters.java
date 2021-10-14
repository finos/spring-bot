package org.finos.symphony.toolkit.workflow.java.converters;

import java.util.function.BiConsumer;

import org.finos.symphony.toolkit.workflow.java.mapping.ChatHandlerExecutor;

/**
 * Collects together all the response converters defined in spring.
 * 
 * @author rob@kite9.com
 *
 */
public interface ResponseConverters extends BiConsumer<Object, ChatHandlerExecutor> {

}
