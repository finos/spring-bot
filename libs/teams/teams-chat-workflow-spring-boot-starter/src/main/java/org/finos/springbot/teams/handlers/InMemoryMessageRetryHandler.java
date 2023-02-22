package org.finos.springbot.teams.handlers;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InMemoryMessageRetryHandler implements MessageRetryHandler {

	private static final Logger LOG = LoggerFactory.getLogger(InMemoryMessageRetryHandler.class);
	
	Queue<MessageRetry> queue = new ConcurrentLinkedQueue<>();
	
	@Override
	public void add(MessageRetry t) {
		queue.add(t);
	}

	@Override
	public Optional<MessageRetry> get() {		
		MessageRetry q;
		if ((q = queue.peek()) != null) {
			LocalDateTime time = q.getCurrentTime().plusSeconds(q.getRetryAfter());
			if (LocalDateTime.now().isAfter(time)) { // retry now
				queue.remove(q);
				return Optional.of(q);
			}else {
				LOG.info("Message not retied, as retry after time {} is not getter than or equal to current time {}", time, LocalDateTime.now());
			}
		}
	
		return Optional.empty();
	}

}
