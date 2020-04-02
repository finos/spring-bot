package com.github.deutschebank.symphony.stream.fixture;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

import com.github.deutschebank.symphony.stream.lq.AbstractLeaderOnlyQueue;

public class BasicLeaderOnlyQueue extends AbstractLeaderOnlyQueue<QueueItem, String> {

	public BasicLeaderOnlyQueue(List<Consumer<QueueItem>> consumers, Consumer<Exception> exceptionHandler, Alert a) {
		super(consumers, exceptionHandler, a);
	}

	@Override
	protected long getTimestamp(QueueItem oldest) {
		return oldest.timestamp;
	}

	@Override
	protected Predicate<? super QueueItem> elementIdPredicate(String eventId) {
		return qi -> qi.id.equals(eventId);
 	}

}
