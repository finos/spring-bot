package org.finos.symphony.toolkit.stream.spring;

import java.util.ArrayList;
import java.util.List;

import org.finos.symphony.toolkit.stream.StreamEventConsumer;
import org.springframework.stereotype.Component;

import com.symphony.api.model.V4Event;

/**
 * This stands in for the functionality of the user's bot.
 * 
 * @author robmoffat
 */
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
