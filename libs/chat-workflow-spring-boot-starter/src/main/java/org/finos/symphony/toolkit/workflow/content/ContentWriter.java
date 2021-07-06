package org.finos.symphony.toolkit.workflow.content;

import java.util.function.Function;

/**
 * Converts a {@link Content} element into a platform-specific string data encoding.
 * 
 * @author rob@kite9.com
 *
 * @param <W>
 * @see MessageParser
 */
public interface ContentWriter extends Function<Content, String> {

}
