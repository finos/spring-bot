package org.finos.symphony.toolkit.stream.welcome;

import org.finos.springbot.entityjson.EntityJson;
import org.finos.springbot.entityjson.ObjectMapperFactory;
import org.finos.springbot.entityjson.VersionSpace;
import org.finos.symphony.toolkit.stream.StreamEventConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.symphony.api.agent.MessagesApi;
import com.symphony.api.id.SymphonyIdentity;
import com.symphony.api.model.UserV2;
import com.symphony.api.model.V4Event;
import com.symphony.api.model.V4RoomCreated;
import com.symphony.api.model.V4UserJoinedRoom;
import com.symphony.api.pod.UsersApi;

/**
 * This class allows you to configure room welcome messages.  This is not configured by default 
 * and needs to be enabled.
 * 
 * @author rob@kite9.com
 *
 */
public class RoomWelcomeEventConsumer implements StreamEventConsumer {
	
	public static final VersionSpace VERSION_SPACE = new VersionSpace(V4RoomCreated.class, "1.0");

	private static final Logger LOG = LoggerFactory.getLogger(RoomWelcomeEventConsumer.class);
	
	private static final String DEFAULT_WELCOME_MESSAGE = "<messageML>"
			+ "<p>Hi, welcome to <b>${entity.stream.roomName}</b></p><br />"
			+ "<p>You can address me here by affixing my name to the beginning of a message, like so:</p><br />"
			+ "<p><mention email=\"${entity.bot.emailAddress}\" /> hi</p>" + "</messageML>";

	private MessagesApi messagesApi;
//	private SymphonyIdentity botIdentity;
	private String welcomeMessageML;
	private UserV2 u;
	private ObjectMapper om;
	
	public RoomWelcomeEventConsumer(MessagesApi messagesApi, UsersApi usersApi, SymphonyIdentity botIdentity, String welcomeMessageML) {
		super();
		this.messagesApi = messagesApi;
//		this.botIdentity = botIdentity;
		this.welcomeMessageML = welcomeMessageML;
		u = usersApi.v2UserGet(null, null, botIdentity.getEmail(), null, true);
		om = new ObjectMapper();
		ObjectMapperFactory.initialize(om, ObjectMapperFactory.extendedSymphonyVersionSpace(
			VERSION_SPACE));
	}
	
	public RoomWelcomeEventConsumer(MessagesApi messagesApi, UsersApi usersApi, SymphonyIdentity botIdentity) {
		this(messagesApi, usersApi, botIdentity, DEFAULT_WELCOME_MESSAGE);
	}
	
	@Override
	public void accept(V4Event t) {
		V4RoomCreated roomCreated = t.getPayload().getRoomCreated();
		V4UserJoinedRoom userJoined = t.getPayload().getUserJoinedRoom();
		String streamId;
		
		EntityJson json = new EntityJson();
		json.put("bot", u);
		
		if (roomCreated != null) {
			json.put("room", roomCreated.getRoomProperties());
			json.put("stream", roomCreated.getStream());
			streamId = roomCreated.getStream().getStreamId();
		} else if (userJoined != null) {
			json.put("user", userJoined.getAffectedUser());
			json.put("stream", userJoined.getStream());
			streamId = userJoined.getStream().getStreamId();
		} else {
			// doesn't need a welcome message
			return;
		}
		
		String jsonStr;
		
		try {
			jsonStr = om.writeValueAsString(json);
			messagesApi.v4StreamSidMessageCreatePost(null, streamId, welcomeMessageML, jsonStr, null, null, null, null);
		} catch (Exception e) {
			LOG.error("Couldn't send welcome message: ", e);
		}
			
		
			
		
	}
	
	
}
