package com.github.deutschebank.symphony.workflow.sources.symphony.messages;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import com.github.detuschebank.symphony.json.EntityJson;
import com.github.deutschebank.symphony.workflow.AbstractNeedsWorkflow;
import com.github.deutschebank.symphony.workflow.Workflow;
import com.github.deutschebank.symphony.workflow.content.Message;
import com.github.deutschebank.symphony.workflow.content.Room;
import com.github.deutschebank.symphony.workflow.content.User;
import com.github.deutschebank.symphony.workflow.content.Word;
import com.github.deutschebank.symphony.workflow.response.Response;
import com.github.deutschebank.symphony.workflow.sources.symphony.SymphonyEventHandler;
import com.github.deutschebank.symphony.workflow.sources.symphony.handlers.EntityJsonConverter;
import com.github.deutschebank.symphony.workflow.sources.symphony.handlers.ResponseHandler;
import com.github.deutschebank.symphony.workflow.sources.symphony.room.SymphonyRooms;
import com.symphony.api.id.SymphonyIdentity;
import com.symphony.api.model.StreamType.TypeEnum;
import com.symphony.api.model.V4Event;
import com.symphony.api.model.V4MessageSent;
import com.symphony.api.pod.UsersApi;

public class PresentationMLHandler extends AbstractNeedsWorkflow implements InitializingBean, SymphonyEventHandler {
	
	private static final Logger LOG = LoggerFactory.getLogger(PresentationMLHandler.class);
	
	SymphonyIdentity botIdentity;
	UsersApi usersApi;
	SimpleMessageParser messageParser;
	EntityJsonConverter jsonConverter;
	List<SimpleMessageConsumer> messageConsumers;
	ResponseHandler rh;
	SymphonyRooms ruBuilder;
		
	public PresentationMLHandler(Workflow wf, SymphonyIdentity botIdentity, UsersApi usersApi, SimpleMessageParser messageParser,
			EntityJsonConverter jsonConverter, List<SimpleMessageConsumer> messageConsumers, ResponseHandler rh, SymphonyRooms ruBuilder) {
		super(wf);
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
				Message words = messageParser.parseMessage(ms.getMessage().getMessage(), ej);
				TypeEnum streamType = TypeEnum.fromValue(ms.getMessage().getStream().getStreamType());
				if (isForThisBot(words, streamType)) {
					Room rr = ruBuilder.loadRoomById(ms.getMessage().getStream().getStreamId());
					User u = ruBuilder.loadUserById(ms.getMessage().getUser().getUserId());
					SimpleMessageAction sma = new SimpleMessageAction(wf, rr, u, words, ej);
					for (SimpleMessageConsumer c : messageConsumers) {
						try {
							
							List<Response> r = c.apply(sma);
							if (r != null) {
								r.stream()
									.forEach(ri -> rh.accept(ri));
							}
							
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
