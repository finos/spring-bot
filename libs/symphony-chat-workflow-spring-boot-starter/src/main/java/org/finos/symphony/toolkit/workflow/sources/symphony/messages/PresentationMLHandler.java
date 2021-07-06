package org.finos.symphony.toolkit.workflow.sources.symphony.messages;

import java.util.List;

import org.finos.symphony.toolkit.json.EntityJson;
import org.finos.symphony.toolkit.workflow.actions.ActionConsumer;
import org.finos.symphony.toolkit.workflow.actions.SimpleMessageAction;
import org.finos.symphony.toolkit.workflow.content.Addressable;
import org.finos.symphony.toolkit.workflow.content.Message;
import org.finos.symphony.toolkit.workflow.content.User;
import org.finos.symphony.toolkit.workflow.content.Word;
import org.finos.symphony.toolkit.workflow.response.handlers.ResponseHandler;
import org.finos.symphony.toolkit.workflow.sources.symphony.SymphonyEventHandler;
import org.finos.symphony.toolkit.workflow.sources.symphony.json.EntityJsonConverter;
import org.finos.symphony.toolkit.workflow.sources.symphony.room.SymphonyRooms;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import com.symphony.api.id.SymphonyIdentity;
import com.symphony.api.model.StreamType.TypeEnum;
import com.symphony.api.model.V4Event;
import com.symphony.api.model.V4MessageSent;
import com.symphony.api.pod.UsersApi;

public class PresentationMLHandler implements InitializingBean, SymphonyEventHandler {
	
	private static final Logger LOG = LoggerFactory.getLogger(PresentationMLHandler.class);
	
	SymphonyIdentity botIdentity;
	UsersApi usersApi;
	MessageMLParser messageParser;
	EntityJsonConverter jsonConverter;
	List<ActionConsumer> messageConsumers;
	ResponseHandler rh;
	SymphonyRooms ruBuilder;
		
	public PresentationMLHandler(SymphonyIdentity botIdentity, UsersApi usersApi, MessageMLParser messageParser,
			EntityJsonConverter jsonConverter, List<ActionConsumer> messageConsumers, ResponseHandler rh, SymphonyRooms ruBuilder) {
		this.botIdentity = botIdentity;
		this.usersApi = usersApi;
		this.messageParser = messageParser;
		this.jsonConverter = jsonConverter;
		this.messageConsumers = messageConsumers;
		this.rh = rh;
		this.ruBuilder = ruBuilder;
	}

	/**
	 * Wont work across pods, probably
	 */
	private String botUserId;
	

	@Override
	public void accept(V4Event t) {
		try {
			V4MessageSent ms = t.getPayload().getMessageSent();
			if ((ms != null) && (!ms.getMessage().getUser().getEmail().equals(botIdentity.getEmail()))) {
				
				// ok, this is a message, and it's from a third party.  Parse it.
				
				EntityJson ej = jsonConverter.readValue(ms.getMessage().getData());
				Message words = messageParser.parse(ms.getMessage().getMessage(), ej);
				TypeEnum streamType = TypeEnum.fromValue(ms.getMessage().getStream().getStreamType());
				if (isForThisBot(words, streamType)) {
					Addressable rr = ruBuilder.loadRoomById(ms.getMessage().getStream().getStreamId());
					User u = ruBuilder.loadUserById(ms.getMessage().getUser().getUserId());
					
					// TODO: multi-user chat (not room)
					rr = rr == null ? u : rr;
					SimpleMessageAction sma = new SimpleMessageAction(rr, u, words, ej);
					for (ActionConsumer c : messageConsumers) {
						try {							
							c.accept(sma);
						} catch (Exception e) {
							LOG.error("Failed to handle consumer "+c, e);
						}
					}
				}
			}
		} catch (Exception e) {
			LOG.error("Couldn't handle event "+t, e);
		}
	}

	public boolean isForThisBot(Message words, TypeEnum streamType) {
		try { 
			boolean addressed = (streamType != TypeEnum.MIM) && (streamType != TypeEnum.ROOM);
			
			addressed = addressed || words.getNth(User.class, 0)
				.filter(b -> b.getId().equals(botUserId))
				.isPresent();
			
			
			addressed = addressed || words.getNth(Word.class, 0)
				.filter(w -> w.getText().startsWith("/"))
				.isPresent();
				
			return addressed;
			
		} catch (Exception e) {
			throw new UnsupportedOperationException(e);
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		botUserId = usersApi.v1UserGet(botIdentity.getEmail(), null, true).getId().toString();
	}


}
