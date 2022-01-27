package org.finos.springbot.symphony.stream.fixture;

import java.util.ArrayList;
import java.util.List;

import org.finos.springbot.symphony.stream.StreamEventConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import com.symphony.api.model.V4Event;

/**
 * This stands in for the functionality of the user's bot.
 * 
 * @author robmoffat
 */
@Component
public class NoddyCallback implements StreamEventConsumer, InitializingBean {
	
	private static final Logger LOG = LoggerFactory.getLogger(NoddyCallback.class);


	private List<V4Event> received = new ArrayList<>();
		
	@Override
	public void accept(V4Event t) {
		received.add(t);
		System.out.println("Noddy received: "+t);
	}

	public List<V4Event> getReceived() {
		return received;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		LOG.info("Initialized Noddy Callback");
	}

}
