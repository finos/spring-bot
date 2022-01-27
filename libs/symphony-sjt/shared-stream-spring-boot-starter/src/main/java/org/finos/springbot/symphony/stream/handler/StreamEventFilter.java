package org.finos.springbot.symphony.stream.handler;

import java.util.Objects;
import java.util.function.Predicate;

import com.symphony.api.model.V4Event;

/**
 * Allows for filtering of events going through the stream handler.
 * 
 * This is useful for filtering out the traffic related to leader election messages, but could
 * be used for other things.
 * 
 * @author rob@kite9.com
 *
 */
public interface StreamEventFilter extends Predicate<V4Event> {

	default StreamEventFilter and(StreamEventFilter other) {
		 Objects.requireNonNull(other);
	     return (t) -> test(t) && other.test(t);
	}

	
}
