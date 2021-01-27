package org.finos.symphony.toolkit.stream.welcome;

import java.util.regex.Pattern;

import org.finos.symphony.toolkit.spring.api.SymphonyApiAutowireConfig;
import org.finos.symphony.toolkit.spring.api.SymphonyApiConfig;
import org.finos.symphony.toolkit.spring.api.builders.CXFApiBuilderConfig;
import org.finos.symphony.toolkit.stream.fixture.NoddyCallback;
import org.finos.symphony.toolkit.stream.handler.SharedStreamHandlerConfig;
import org.finos.symphony.toolkit.stream.single.SharedStreamSingleBotConfig;
import org.finos.symphony.toolkit.stream.welcome.RoomWelcomeEventConsumerIT.TestContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.symphony.api.agent.MessagesApi;
import com.symphony.api.id.SymphonyIdentity;
import com.symphony.api.model.V4Event;
import com.symphony.api.model.V4Payload;
import com.symphony.api.model.V4RoomCreated;
import com.symphony.api.model.V4RoomProperties;
import com.symphony.api.model.V4Stream;
import com.symphony.api.model.V4User;
import com.symphony.api.model.V4UserJoinedRoom;
import com.symphony.api.pod.UsersApi;


@ExtendWith(SpringExtension.class)

@SpringBootTest(
	properties = { 
			"logging.level.org.finos.symphony.toolkit=debug"}, 
	classes={
			TestContext.class,
			NoddyCallback.class,
			CXFApiBuilderConfig.class,
			SymphonyApiConfig.class,
			SymphonyApiAutowireConfig.class,
			SharedStreamHandlerConfig.class,
 			SharedStreamSingleBotConfig.class
		}
	)
@ActiveProfiles("develop")
public class RoomWelcomeEventConsumerIT {

	private static final String WELCOME_MESSAGE = "<messageML>Welcome!</messageML>";

	private static final String NEW_ROOM_STREAM_ID = "Cscf+rSZRtGaOUrhkelBaH///o6ry5/5dA==";

	public static class TestContext {
		
		/**
		 * Needed since we don't load spring-web.
		 * @return
		 */
		@Bean 
		public ObjectMapper objectMapper() {
			
			return new ObjectMapper();
		}
		
	}
	
	@MockBean
	MessagesApi messages;
	
	@Autowired
	UsersApi users;
	
	@Autowired
	SymphonyIdentity bot;
	
	@Test
	public void testRoomCreated() {
		RoomWelcomeEventConsumer rwec = new RoomWelcomeEventConsumer(messages, users, bot, WELCOME_MESSAGE);

		V4Event event = new V4Event().payload(
				new V4Payload().roomCreated(
					new V4RoomCreated()
						.roomProperties(new V4RoomProperties().name("Big Room"))
						.stream(new V4Stream().streamId(NEW_ROOM_STREAM_ID))));
		
		rwec.accept(event);
		
		Mockito.verify(messages, Mockito.times(1)).v4StreamSidMessageCreatePost(
			Mockito.isNull(), 
			Mockito.matches(Pattern.quote(NEW_ROOM_STREAM_ID)), 
			Mockito.matches(Pattern.quote(WELCOME_MESSAGE)), 
			Mockito.isNotNull(), 
			Mockito.isNull(), 
			Mockito.isNull(), 
			Mockito.isNull(), 
			Mockito.isNull());
		
		Mockito.clearInvocations(messages);
	}
	
	@Test
	public void testUserAdded() {
		RoomWelcomeEventConsumer rwec = new RoomWelcomeEventConsumer(messages, users, bot, WELCOME_MESSAGE);

		V4Event event = new V4Event().payload(
				new V4Payload().userJoinedRoom(
					new V4UserJoinedRoom()
						.affectedUser(new V4User().displayName("Gordon Bennett"))
						.stream(new V4Stream().streamId(NEW_ROOM_STREAM_ID))));
		
		rwec.accept(event);
		
		Mockito.verify(messages, Mockito.times(1)).v4StreamSidMessageCreatePost(
			Mockito.isNull(), 
			Mockito.matches(Pattern.quote(NEW_ROOM_STREAM_ID)), 
			Mockito.matches(Pattern.quote(WELCOME_MESSAGE)), 
			Mockito.isNotNull(), 
			Mockito.isNull(), 
			Mockito.isNull(), 
			Mockito.isNull(), 
			Mockito.isNull());
	
		Mockito.clearInvocations(messages);
	}
}
