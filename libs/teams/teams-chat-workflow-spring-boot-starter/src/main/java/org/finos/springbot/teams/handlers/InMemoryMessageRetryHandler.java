package org.finos.springbot.teams.handlers;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class InMemoryMessageRetryHandler implements MessageRetryHandler {

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
			}
		}
	
		return Optional.empty();
	}
	
	public int queueSize() {
		return queue.size();
	}
	

}
