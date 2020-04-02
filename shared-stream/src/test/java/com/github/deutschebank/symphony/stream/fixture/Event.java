package com.github.deutschebank.symphony.stream.fixture;

import org.junit.Assert;

import com.github.deutschebank.symphony.stream.msg.Participant;

/**
 * Something that needs to be processed on the queue
 * @author robmoffat
 *
 */
public class Event {

	String id;
	Participant processedby;
	
	public Event(String id) {
		super();
		this.id = id;
	}
	
	boolean processed;
	
	public synchronized void process(Participant p) {
		if (processedby != null) {
			Assert.fail("Event "+id+" already processed by "+processedby+", now "+p);
		} else {
			processedby = p;
			System.out.println("Event "+id+" processed by "+p);
		}
	}
}
