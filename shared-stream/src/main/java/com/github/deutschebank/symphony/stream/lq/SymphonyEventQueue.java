package com.github.deutschebank.symphony.stream.lq;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

import com.github.deutschebank.symphony.stream.StreamEventConsumer;
import com.symphony.api.model.V4Event;

/**
 * Symphony version of the queue, which understands V4Events.
 */
public class SymphonyEventQueue extends AbstractLeaderOnlyQueue<V4Event, String> {

	public SymphonyEventQueue(List<StreamEventConsumer> consumers, Consumer<Exception> exceptionHandler, Alert a) {
		super(convert(consumers), exceptionHandler, a);
	}

	private static List<Consumer<V4Event>> convert(List<StreamEventConsumer> consumers) {
		return new ArrayList<Consumer<V4Event>>(consumers);
	}


	protected Predicate<? super V4Event> elementIdPredicate(String eventId) {
		return e -> e.getId().equals(eventId);
	}

	protected long getTimestamp(V4Event oldest) {
		return oldest.getTimestamp();
	}

}
