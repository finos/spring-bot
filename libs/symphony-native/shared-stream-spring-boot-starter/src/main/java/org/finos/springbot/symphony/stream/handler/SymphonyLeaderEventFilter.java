package org.finos.springbot.symphony.stream.handler;

import java.util.Optional;

import org.finos.springbot.symphony.stream.Participant;
import org.finos.springbot.symphony.stream.StreamEventConsumer;
import org.finos.springbot.symphony.stream.log.LogMessage;
import org.finos.springbot.symphony.stream.log.SharedLog;

import com.symphony.api.model.V4Event;

/**
 * This is a filter for events that will only allow the events to be processed by a wrapped
 * {@link StreamEventConsumer} if the current participant is leader.
 * 
 * Changes to leadership are communicated on the event stream.
 * 
 * @author robmoffat
 *
 */
public class SymphonyLeaderEventFilter implements StreamEventFilter {
	
	protected final Participant self;
	protected final SharedLog messageHandler;

	public SymphonyLeaderEventFilter(Participant self, SharedLog messageHandler) {
		this.messageHandler = messageHandler;
		this.self = self;
	}
	
	@Override
	public boolean test(V4Event t) {
		Optional<LogMessage> msg = messageHandler.handleEvent(t);
		if (msg.isPresent()) {
			return false;
		} else {
			return isActive();
		}
	}

	public boolean isActive() {
		return messageHandler.isLeader(self);
	}
}
