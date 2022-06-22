package org.finos.springbot.symphony.conversations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.finos.springbot.symphony.SymphonyMockConfiguration;
import org.finos.springbot.symphony.SymphonyWorkflowConfig;
import org.finos.springbot.symphony.content.SymphonyRoom;
import org.finos.springbot.symphony.content.SymphonyUser;
import org.finos.springbot.tests.controller.AbstractHandlerMappingTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import com.symphony.bdk.core.service.message.MessageService;
import com.symphony.bdk.core.service.stream.StreamService;
import com.symphony.bdk.gen.api.model.ConversationSpecificStreamAttributes;
import com.symphony.bdk.gen.api.model.MemberInfo;
import com.symphony.bdk.gen.api.model.RoomSpecificStreamAttributes;
import com.symphony.bdk.gen.api.model.RoomSystemInfo;
import com.symphony.bdk.gen.api.model.Stream;
import com.symphony.bdk.gen.api.model.StreamAttributes;
import com.symphony.bdk.gen.api.model.StreamType;
import com.symphony.bdk.gen.api.model.StreamType.TypeEnum;
import com.symphony.bdk.gen.api.model.V3RoomAttributes;
import com.symphony.bdk.gen.api.model.V3RoomDetail;
import com.symphony.bdk.gen.api.model.V3RoomSearchResults;

@SpringBootTest(classes = { 
	SymphonyMockConfiguration.class, 
	SymphonyWorkflowConfig.class,
})
@ActiveProfiles(value = "symphony")
public class SymphonyConversationsTest {
	
	@Autowired
	SymphonyConversationsImpl ruBuilder;
	
	@MockBean
	StreamService streamsApi;

	@MockBean
	MessageService messagesApi;
	
	@Test
	public void testEnsureRoom() {
		ruBuilder.setDefaultAdministrators(Collections.singletonList(new SymphonyUser(1111l)));
		
		
//		// create room
		
		List<Long> l = new ArrayList<Long>();
		l.add(AbstractHandlerMappingTest.ROB_EXAMPLE_ID);	// robski
		l.add(AbstractHandlerMappingTest.BOT_ID); // the bot
		

		
		when(streamsApi.listStreams(Mockito.any(), Mockito.any()))
			.thenReturn(
				Arrays.asList(
					new StreamAttributes()
					.roomAttributes(new RoomSpecificStreamAttributes()
						.name("Some Test Room"))
					.id("abc123")
					.streamType(new StreamType().type(TypeEnum.ROOM)),
				
					new StreamAttributes()
						.streamAttributes(new ConversationSpecificStreamAttributes()
							.members(l))
						.id("283746")
						.streamType(new StreamType().type(TypeEnum.IM))));
		
		when(streamsApi.create(any(V3RoomAttributes.class)))
			.then(a -> new V3RoomDetail()
				.roomSystemInfo(new RoomSystemInfo().id("456"))
				.roomAttributes(new V3RoomAttributes()._public(false).name("Some Test Room").description("Still Bogus")));
	
		
		when(streamsApi.searchRooms(Mockito.any()))
			.then(a -> new V3RoomSearchResults().rooms(Collections.emptyList()));
		
		SymphonyRoom rd = new SymphonyRoom("Some Test Room", null);
		
		SymphonyUser su = new SymphonyUser(AbstractHandlerMappingTest.ROB_EXAMPLE_ID);
		
		SymphonyRoom out = ruBuilder.ensureChat(rd, Collections.singletonList(su), SymphonyConversations.simpleMeta("Automated Test Room Created", true));
		assertEquals("Some Test Room", out.getName());
		assertEquals(2, ruBuilder.getAllAddressables().size());
		assertEquals("456", out.getKey());

		// return members
		when(streamsApi.listRoomMembers(Mockito.anyString()))
			.thenReturn(Arrays.asList(new MemberInfo().id(AbstractHandlerMappingTest.ROB_EXAMPLE_ID).owner(true)));
			
		List<SymphonyUser> chatMembers = ruBuilder.getChatMembers(out);
		Assertions.assertEquals(
			Collections.singletonList(new SymphonyUser(AbstractHandlerMappingTest.ROB_EXAMPLE_ID, 
					AbstractHandlerMappingTest.ROB_NAME, 
					AbstractHandlerMappingTest.ROB_EXAMPLE_EMAIL)), 
					chatMembers);
	}
	
	@Test
	public void testGetUserStream() {
		when(streamsApi.create(Mockito.anyList()))
			.thenAnswer(c -> new Stream().id("123"));
		
		SymphonyUser rd = new SymphonyUser("Robski mo", "rob@example.com");
		String someStream = ruBuilder.getStreamFor(rd);
		Assertions.assertEquals("123", someStream);
	}
}
