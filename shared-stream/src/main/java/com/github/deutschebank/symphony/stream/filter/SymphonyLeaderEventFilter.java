package com.github.deutschebank.symphony.stream.filter;

import java.util.Optional;
import java.util.function.Consumer;

import com.github.deutschebank.symphony.stream.Participant;
import com.github.deutschebank.symphony.stream.StreamEventConsumer;
import com.github.deutschebank.symphony.stream.log.SharedLog;
import com.github.deutschebank.symphony.stream.log.LogMessage;
import com.github.deutschebank.symphony.stream.log.LogMessageHandler;
import com.github.deutschebank.symphony.stream.log.LogMessageType;
import com.symphony.api.model.V4Event;
import com.symphony.api.model.V4Message;
import com.symphony.api.model.V4MessageSent;

/**
 * This is a filter for events that will only allow the events to be processed by a wrapped
 * {@link StreamEventConsumer} if the current participant is leader.
 * 
 * Changes to leadership are communicated on the event stream.
 * 
 * @author robmoffat
 *
 */
public class SymphonyLeaderEventFilter implements StreamEventConsumer {
	
	StreamEventConsumer next;
	boolean passing;
	SharedLog sl;
	Participant self;
	LogMessageHandler messageHandler;
	Consumer<LogMessage> controlEventConsumer;

	public SymphonyLeaderEventFilter(StreamEventConsumer next, boolean startAsLeader, Participant self, LogMessageHandler messageHandler, Consumer<LogMessage> consumer) {
		this.next = next;
		this.passing = startAsLeader;
		this.messageHandler = messageHandler;
		this.self = self;
		this.controlEventConsumer = consumer;
	}

	@Override
	public void accept(V4Event t) {
		if (messageHandler.isLeaderMessage(t)) {
			V4MessageSent ms = t.getPayload().getMessageSent();
			V4Message m = ms.getMessage();
			Optional<LogMessage> slm = messageHandler.readMessage(m);
			if (slm.isPresent()) {
				LogMessage logMessage = slm.get();
				if (logMessage.getMessageType() == LogMessageType.LEADER) {
					passing = self.equals(logMessage.getParticipant());
				}
				controlEventConsumer.accept(logMessage);
			}
		} else if (messageHandler.isParticipantMessage(t)) {
			V4MessageSent ms = t.getPayload().getMessageSent();
			V4Message m = ms.getMessage();
			Optional<LogMessage> slm = messageHandler.readMessage(m);
			if (slm.isPresent()) {
				controlEventConsumer.accept(slm.get());
			}
		} else if (passing) {
			next.accept(t);
		}
	}
}
