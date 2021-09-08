package org.finos.symphony.toolkit.workflow;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.finos.symphony.toolkit.workflow.sources.symphony.content.SymphonyRoom;
import org.finos.symphony.toolkit.workflow.sources.symphony.content.SymphonyUser;
import org.finos.symphony.toolkit.workflow.sources.symphony.conversations.SymphonyConversations;
import org.finos.symphony.toolkit.workflow.sources.symphony.conversations.SymphonyConversationsImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import com.symphony.api.model.ConversationSpecificStreamAttributes;
import com.symphony.api.model.MemberInfo;
import com.symphony.api.model.MembershipList;
import com.symphony.api.model.RoomSpecificStreamAttributes;
import com.symphony.api.model.RoomSystemInfo;
import com.symphony.api.model.Stream;
import com.symphony.api.model.StreamAttributes;
import com.symphony.api.model.StreamList;
import com.symphony.api.model.StreamType;
import com.symphony.api.model.V3RoomAttributes;
import com.symphony.api.model.V3RoomDetail;
import com.symphony.api.model.V3RoomSearchResults;
import com.symphony.api.model.StreamType.TypeEnum;
import com.symphony.api.model.UserIdList;

public class TestRoomAndUsersBuilder extends AbstractMockSymphonyTest {
	
	@Autowired
	SymphonyConversationsImpl ruBuilder;
	
	@Test
	public void testEnsureRoom() {
		ruBuilder.setDefaultAdministrators(Collections.singletonList(new SymphonyUser(1111l)));
		
		
		// create room
		when(streamsApi.v1StreamsListPost(Mockito.isNull(), Mockito.any(), Mockito.eq(0), Mockito.eq(50)))
			.thenAnswer(c -> {
				StreamList out = new StreamList();
				out.add(new StreamAttributes()
					.roomAttributes(new RoomSpecificStreamAttributes()
						.name("Some Test Room"))
					.id("abc123")
					.streamType(new StreamType().type(TypeEnum.ROOM)));
				
				UserIdList l = new UserIdList();
				l.add(765l);	// robski
				l.add(654321l); // the bot
				
				out.add(new StreamAttributes()
						.streamAttributes(new ConversationSpecificStreamAttributes()
							.members(l))
						.id("283746")
						.streamType(new StreamType().type(TypeEnum.IM)));
				
				return out;
			});
		
		when(streamsApi.v3RoomCreatePost(any(), isNull()))
			.then(a -> new V3RoomDetail()
				.roomSystemInfo(new RoomSystemInfo().id("456"))
				.roomAttributes(new V3RoomAttributes()._public(false).name("Some Test Room").description("Still Bogus")));
	
		
		when(streamsApi.v3RoomSearchPost(Mockito.any(), Mockito.isNull(), Mockito.isNull(), Mockito.isNull()))
			.then(a -> new V3RoomSearchResults().rooms(Collections.emptyList()));
		
		SymphonyRoom rd = new SymphonyRoom("Some Test Room", null);
		
		SymphonyUser su = new SymphonyUser(2342l);
		
		SymphonyRoom out = ruBuilder.ensureChat(rd, Collections.singletonList(su), SymphonyConversations.simpleMeta("Automated Test Room Created", true));
		assertEquals("Some Test Room", out.getName());
		assertEquals(2, ruBuilder.getAllConversations().size());
		assertEquals("456", out.getStreamId());

		// return members
		MembershipList ml = new MembershipList();
		ml.add(new MemberInfo().id(123l).owner(true));
		when(rmApi.v1RoomIdMembershipListGet(Mockito.anyString(), Mockito.isNull())).thenReturn(ml);
	
		Assertions.assertEquals(
			Collections.singletonList(new SymphonyUser(123l, "Roberto Banquet", "r@example.com")), 
					ruBuilder.getChatMembers(out));
	}
	
	@Test
	public void testGetUserStream() {
		when(streamsApi.v1ImCreatePost(Mockito.any(),Mockito.isNull()))
			.thenAnswer(c -> new Stream().id("123"));
		
		SymphonyUser rd = new SymphonyUser("Robski mo", "rob@example.com");
		String someStream = ruBuilder.getStreamFor(rd);
		Assertions.assertEquals("123", someStream);
	}
}
