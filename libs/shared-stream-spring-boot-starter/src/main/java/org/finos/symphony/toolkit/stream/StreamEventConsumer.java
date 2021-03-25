package org.finos.symphony.toolkit.stream;

import java.util.function.Consumer;

import com.symphony.api.model.V4Event;

/**
 * Create spring beans with this interface to allow reading from the event stream.
 * 
 * @author robmoffat
 *
 */
public interface StreamEventConsumer extends Consumer<V4Event>{

}
