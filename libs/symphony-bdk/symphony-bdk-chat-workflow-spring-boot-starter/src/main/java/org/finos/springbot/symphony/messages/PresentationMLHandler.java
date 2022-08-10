package org.finos.springbot.symphony.messages;

import java.util.List;

import com.symphony.bdk.gen.api.model.StreamType;
import org.finos.springbot.entityjson.EntityJson;
import org.finos.springbot.symphony.content.serialization.MessageMLParser;
import org.finos.springbot.symphony.conversations.SymphonyConversations;
import org.finos.springbot.workflow.actions.Action;
import org.finos.springbot.workflow.actions.SimpleMessageAction;
import org.finos.springbot.workflow.actions.consumers.ActionConsumer;
import org.finos.springbot.workflow.content.Addressable;
import org.finos.springbot.workflow.content.Message;
import org.finos.springbot.workflow.content.User;
import org.finos.springbot.workflow.data.EntityJsonConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;

import com.symphony.bdk.gen.api.model.V4MessageSent;
import com.symphony.bdk.spring.events.RealTimeEvent;

public class PresentationMLHandler implements ApplicationListener<RealTimeEvent<V4MessageSent>> {
	
	private static final Logger LOG = LoggerFactory.getLogger(PresentationMLHandler.class);
	
	String botUsername;
	MessageMLParser messageParser;
	EntityJsonConverter jsonConverter;
	List<ActionConsumer> messageConsumers;
	SymphonyConversations ruBuilder;
		
	public PresentationMLHandler(MessageMLParser messageParser,
			EntityJsonConverter jsonConverter, 
			List<ActionConsumer> messageConsumers, 
			SymphonyConversations ruBuilder,
			String botUsername) {
		this.messageParser = messageParser;
		this.jsonConverter = jsonConverter;
		this.messageConsumers = messageConsumers;
		this.ruBuilder = ruBuilder;
		this.botUsername = botUsername;
	}

	@Override
	public void onApplicationEvent(RealTimeEvent<V4MessageSent> t) {
		try {
			V4MessageSent ms = t.getSource();
			if ((ms != null) && (!botUsername.equals(ms.getMessage().getUser().getUsername()))) {
				
				// ok, this is a message, and it's from a third party.  Parse it.
				
				EntityJson ej = jsonConverter.readValue(ms.getMessage().getData());
				Message words = messageParser.apply(ms.getMessage().getMessage(), ej);
				Addressable rr = null;
				if(ms.getMessage().getStream().getStreamType().equals(StreamType.TypeEnum.ROOM)) {
					rr = ruBuilder.loadRoomById(ms.getMessage().getStream().getStreamId());
				}
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
