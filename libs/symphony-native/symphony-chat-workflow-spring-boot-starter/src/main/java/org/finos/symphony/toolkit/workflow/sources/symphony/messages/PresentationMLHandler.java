package org.finos.symphony.toolkit.workflow.sources.symphony.messages;

import java.util.List;

import org.finos.springbot.workflow.actions.Action;
import org.finos.springbot.workflow.actions.SimpleMessageAction;
import org.finos.springbot.workflow.actions.consumers.ActionConsumer;
import org.finos.springbot.workflow.content.Addressable;
import org.finos.springbot.workflow.content.Message;
import org.finos.springbot.workflow.content.User;
import org.finos.symphony.toolkit.json.EntityJson;
import org.finos.symphony.toolkit.stream.StreamEventConsumer;
import org.finos.symphony.toolkit.workflow.sources.symphony.conversations.SymphonyConversations;
import org.finos.symphony.toolkit.workflow.sources.symphony.json.EntityJsonConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.symphony.api.id.SymphonyIdentity;
import com.symphony.api.model.V4Event;
import com.symphony.api.model.V4MessageSent;

public class PresentationMLHandler implements StreamEventConsumer {
	
	private static final Logger LOG = LoggerFactory.getLogger(PresentationMLHandler.class);
	
	SymphonyIdentity botIdentity;
	MessageMLParser messageParser;
	EntityJsonConverter jsonConverter;
	List<ActionConsumer> messageConsumers;
	SymphonyConversations ruBuilder;
		
	public PresentationMLHandler(MessageMLParser messageParser,
			EntityJsonConverter jsonConverter, 
			List<ActionConsumer> messageConsumers, 
			SymphonyConversations ruBuilder,
			SymphonyIdentity botIdentity) {
		this.messageParser = messageParser;
		this.jsonConverter = jsonConverter;
		this.messageConsumers = messageConsumers;
		this.ruBuilder = ruBuilder;
		this.botIdentity = botIdentity;
	}

	@Override
	public void accept(V4Event t) {
		try {
			V4MessageSent ms = t.getPayload().getMessageSent();
			if ((ms != null) && (!ms.getMessage().getUser().getEmail().equals(botIdentity.getEmail()))) {
				
				// ok, this is a message, and it's from a third party.  Parse it.
				
				EntityJson ej = jsonConverter.readValue(ms.getMessage().getData());
				Message words = messageParser.apply(ms.getMessage().getMessage(), ej);
				Addressable rr = ruBuilder.loadRoomById(ms.getMessage().getStream().getStreamId());
				User u = ruBuilder.loadUserById(ms.getMessage().getUser().getUserId());
				
				// TODO: multi-user chat (not room)
				rr = rr == null ? u : rr;
				SimpleMessageAction sma = new SimpleMessageAction(rr, u, words, ej);
				try {
					Action.CURRENT_ACTION.set(sma);
					for (ActionConsumer c : messageConsumers) {
						c.accept(sma);
					}
				} finally {
					Action.CURRENT_ACTION.set(Action.NULL_ACTION);
				}
			}
		} catch (Exception e) {
			LOG.error("Couldn't handle event "+t, e);
		}
	}


}
