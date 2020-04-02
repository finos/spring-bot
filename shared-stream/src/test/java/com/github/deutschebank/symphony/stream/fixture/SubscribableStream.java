package com.github.deutschebank.symphony.stream.fixture;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Supplier;

import com.github.deutschebank.symphony.stream.cluster.ClusterMember;

/**
 * Sends out messages to it's subscribers, if they are connected.
 * 
 * @author robmoffat
 *
 */
public class SubscribableStream<ID> {
	
	Set<ClusterMember<ID>> receivers = new HashSet<ClusterMember<ID>>();
	Connectivity conn;
	boolean running = true;
	int maxGapMs;
	Random r = new Random();
	Supplier<ID> eventStream;
	List<ID> allEvents = new ArrayList<>();
	
	public SubscribableStream(Connectivity conn, int maxGapMs, Supplier<ID> eventStream) {
		super();
		this.conn = conn;
		this.maxGapMs = maxGapMs;
		this.eventStream = eventStream;
	}

	public void subscribe(ClusterMember<ID> cm) {
		receivers.add(cm);
	}
	
	public void stop() {
		running = false;
	}
	
	public void start() {
		running = true;
		new Thread(() -> {
			
			while (running) {
				try {
					Thread.sleep(r.nextInt(maxGapMs));
				} catch (InterruptedException e) {
				}
				
				ID event = eventStream.get();
				allEvents.add(event);
				
				for (ClusterMember<ID> cm : receivers) {
					if (!conn.isIsolated(cm.getSelfDetails())) {
						cm.receiveEvent(event);
					}
				}
				
			}
		});
	}

	public List<ID> getAllEvents() {
		return allEvents;
	}

	public void setAllEvents(List<ID> allEvents) {
		this.allEvents = allEvents;
	}
}
