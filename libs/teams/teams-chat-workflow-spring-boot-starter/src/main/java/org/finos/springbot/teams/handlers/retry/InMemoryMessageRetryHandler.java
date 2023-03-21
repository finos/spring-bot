package org.finos.springbot.teams.handlers.retry;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InMemoryMessageRetryHandler extends BasicMessageRetryHandler {

	private static final Logger LOG = LoggerFactory.getLogger(InMemoryMessageRetryHandler.class);
	
	private Queue<MessageRetry> queue = new ConcurrentLinkedQueue<>();
	
	@Override
	public void add(MessageRetry t) {
		queue.add(t);
	}
	
	@Override 
	public void clearAll() {
		while(queue.poll()!=null);
	}

	@Override
	public Optional<MessageRetry> get() {		
		MessageRetry q;
		if ((q = queue.peek()) != null) {
			if (LocalDateTime.now().isAfter(q.getRetryTime())) { // retry now
				queue.remove(q);
				return Optional.of(q);
			}else {
				LOG.info("Message not retried, as retry time {} for message has not passed the current time {}", q.getRetryTime(), LocalDateTime.now());
			}
		}
	
		return Optional.empty();
	}
	

}
