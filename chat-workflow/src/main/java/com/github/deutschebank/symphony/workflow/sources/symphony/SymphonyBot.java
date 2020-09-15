package com.github.deutschebank.symphony.workflow.sources.symphony;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

import com.github.deutschebank.symphony.stream.StreamEventConsumer;
import com.symphony.api.id.SymphonyIdentity;
import com.symphony.api.model.V4Event;

/**
 * Handles workflow events originating on symphony.  Simply passes all events on to dedicated
 * event handlers.
 * 
 * @author Rob Moffat
 *
 */
public class SymphonyBot implements StreamEventConsumer {

	private static final Logger LOG = LoggerFactory.getLogger(SymphonyBot.class);

	SymphonyIdentity botIdentity;
	List<SymphonyEventHandler> eventHandlers;
	
	public SymphonyBot(SymphonyIdentity botIdentity, List<SymphonyEventHandler> eventHandlers) {
		super();
		this.botIdentity = botIdentity;
		this.eventHandlers = eventHandlers;
	}

	public void accept(V4Event event) {
		try {
			LOG.info("Beginning handle for "+event);
			for (SymphonyEventHandler symphonyEventHandler : eventHandlers) {
				symphonyEventHandler.accept(event);
			}
		} catch (Throwable e) {
			LOG.error("Couldn't handle symphony event: ", e);
		}
	}

	@Scheduled(fixedRate=10000)
	public void doSomething() {
		
	}
	
}
