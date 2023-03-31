package org.finos.springbot.teams.handlers.retry;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.finos.springbot.teams.conversations.TeamsConversations;

public class InMemoryRetryingActivityHandler extends AbstractRetryingActivityHandler  {

	private Queue<MessageRetry> queue = new ConcurrentLinkedQueue<>();
	
	public InMemoryRetryingActivityHandler(TeamsConversations tc) {
		super(tc);
	}
	
	protected void add(MessageRetry retry) {
		queue.add(retry);
	}
	
	public Optional<MessageRetry> get() {
		MessageRetry q;
		if ((q = queue.peek()) != null) {
			if (LocalDateTime.now().isAfter(q.getRetryTime())) { // retry now
				queue.remove(q);
				return Optional.of(q);
			} else {
				LOG.info("Message not retried, as retry time {} for message has not passed the current time {}",
						q.getRetryTime(), LocalDateTime.now());
			}
		}

		return Optional.empty();
	}

}
