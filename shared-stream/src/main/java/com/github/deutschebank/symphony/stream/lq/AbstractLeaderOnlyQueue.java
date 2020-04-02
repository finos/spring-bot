package com.github.deutschebank.symphony.stream.lq;

import java.util.Deque;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.function.Consumer;
import java.util.function.Predicate;
/**
 * Receives messages, and queues them up internally.
 * Will only process messages if it is the leader.
 * Removes messages if it receives notification that they have already been processed.
 * Generates an alert if the queue is too full.
 * By default provides a policy that elements must be processed in < 1 min
 * 
 * @author robmoffat
 *
 */
public abstract class AbstractLeaderOnlyQueue<X, ID> implements Consumer<X>, LeaderOnlyQueue<X, ID> {
	
	@FunctionalInterface
	public static interface Alert {
		
		public void queueTooFull();
		
	}
	
	protected Deque<X> backingDeque;
	protected boolean leader;
	protected List<Consumer<X>> consumers;
	protected Consumer<Exception> exceptionHandler;
	protected Alert a;

	public AbstractLeaderOnlyQueue(List<Consumer<X>> consumers, Consumer<Exception> exceptionHandler, Alert a) {
		this.backingDeque = new ConcurrentLinkedDeque<X>();
		this.leader = false;
		this.consumers = consumers;
		this.exceptionHandler = exceptionHandler;
		this.a = a;
	}

	@Override
	public synchronized void accept(X t) {
		backingDeque.addLast(t);
		if (leader) {
			exhaustQueue();
		} else if (queueLimitExceeded()) {
			a.queueTooFull();
		}
	}
	
	protected boolean queueLimitExceeded() {
		X oldest = backingDeque.peekFirst();
		long ts = getTimestamp(oldest);
		long age = System.currentTimeMillis() - ts;
		return age > getMaxAge();	
	}

	/**
	 * Default is 1 minute
	 */
	protected long getMaxAge() {
		return 1000*60;
	}

	protected abstract long getTimestamp(X oldest);
	
	private void exhaustQueue() {
		while (backingDeque.size() > 0) {
			X item = backingDeque.removeFirst();
			consumers.forEach(c -> processItem(item, c));
		}
	}

	protected void processItem(X item, Consumer<X> c) {
		try {
			c.accept(item);
		} catch (Exception e) {
			exceptionHandler.accept(e);
		}
	}

	@Override
	public synchronized void makeLeader() {
		this.leader = true;
		exhaustQueue();
	}
	
	@Override
	public synchronized void noLongerLeader() {
		this.leader = false;
	}
	
	public void remove(ID eventId) {
		find(eventId).ifPresent(x -> backingDeque.remove(x));
	}

	protected Optional<X> find(ID eventId) {
		return backingDeque.stream().filter(elementIdPredicate(eventId)).findFirst();
	}

	protected abstract Predicate<? super X> elementIdPredicate(ID eventId);
}
