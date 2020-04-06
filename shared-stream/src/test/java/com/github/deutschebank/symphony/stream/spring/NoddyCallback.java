package com.github.deutschebank.symphony.stream.spring;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.github.deutschebank.symphony.stream.StreamEventConsumer;
import com.symphony.api.model.V4Event;

@Component
public class NoddyCallback implements StreamEventConsumer {

	private List<V4Event> received = new ArrayList<>();
	
	@Override
	public void accept(V4Event t) {
		received.add(t);
		System.out.println("Noddy received: "+t);
	}

	public List<V4Event> getReceived() {
		return received;
	}

}
