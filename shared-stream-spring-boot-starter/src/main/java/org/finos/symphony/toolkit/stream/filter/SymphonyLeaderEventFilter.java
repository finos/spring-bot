package org.finos.symphony.toolkit.stream.filter;

import java.util.Optional;
import java.util.function.Consumer;

import org.finos.symphony.toolkit.stream.Participant;
import org.finos.symphony.toolkit.stream.StreamEventConsumer;
import org.finos.symphony.toolkit.stream.log.LogMessage;
import org.finos.symphony.toolkit.stream.log.LogMessageHandler;
import org.finos.symphony.toolkit.stream.log.LogMessageType;

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
	
	protected final StreamEventConsumer next;
	protected boolean active;
	protected final Participant self;
	protected final LogMessageHandler messageHandler;
	protected final Consumer<LogMessage> controlEventConsumer;

	public SymphonyLeaderEventFilter(StreamEventConsumer next, boolean startAsLeader, Participant self, LogMessageHandler messageHandler, Consumer<LogMessage> consumer) {
		this.next = next;
		this.active = startAsLeader;
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
					active = self.equals(logMessage.getParticipant());
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
		} else if (active) {
			next.accept(t);
		}
	}

	public boolean isActive() {
		return active;
	}
}
