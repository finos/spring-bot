package org.finos.springbot.teams.handlers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
	public List<MessageRetry> get() {
		LOG.info("Retry message queue size {}.", queue.size());
		List<MessageRetry> retries = new ArrayList<>();
		MessageRetry q;
		while ((q = queue.peek()) != null) {
			LocalDateTime time = q.getCurrentTime().plusSeconds(q.getRetryAfter());
			if (LocalDateTime.now().isAfter(time)) { // retry now
				queue.remove(q);
				retries.add(q);
			}else {
				break;//to pass the pick time... Retry the messages in next cycle
			}
		}
	
		return retries;
	}

}
