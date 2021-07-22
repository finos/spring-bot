package org.finos.symphony.toolkit.workflow;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.finos.symphony.toolkit.workflow.sources.symphony.content.SymphonyRoom;
import org.finos.symphony.toolkit.workflow.sources.symphony.content.SymphonyUser;
import org.finos.symphony.toolkit.workflow.sources.symphony.room.SymphonyRooms;
import org.finos.symphony.toolkit.workflow.sources.symphony.room.SymphonyRoomsImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import com.symphony.api.model.MemberInfo;
import com.symphony.api.model.MembershipList;
import com.symphony.api.model.RoomSpecificStreamAttributes;
import com.symphony.api.model.RoomSystemInfo;
import com.symphony.api.model.Stream;
import com.symphony.api.model.StreamAttributes;
import com.symphony.api.model.StreamList;
import com.symphony.api.model.V3RoomAttributes;
import com.symphony.api.model.V3RoomDetail;
import com.symphony.api.model.V3RoomSearchResults;

public class TestRoomAndUsersBuilder extends AbstractMockSymphonyTest {
	
	@Autowired
	SymphonyRoomsImpl ruBuilder;
	
	@Test
	public void testEnsureRoom() {
		ruBuilder.setDefaultAdministrators(Collections.singletonList(new SymphonyUser(1111l)));
		
		
		// create room
		when(streamsApi.v1StreamsListPost(Mockito.isNull(), Mockito.any(), Mockito.eq(0), Mockito.eq(0)))
			.thenAnswer(c -> {
				StreamList out = new StreamList();
				out.add(new StreamAttributes()
					.roomAttributes(new RoomSpecificStreamAttributes()
						.name("Some Test Room"))
					.id("abc123"));
				
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
		
		SymphonyRoom out = ruBuilder.ensureRoom(rd, Collections.singletonList(su), SymphonyRooms.simpleMeta("Automated Test Room Created", true));
		assertEquals("Some Test Room", out.getName());
		assertEquals(1, ruBuilder.getAllRooms().size());
		assertEquals("456", out.getStreamId());

		// return members
		MembershipList ml = new MembershipList();
		ml.add(new MemberInfo().id(123l).owner(true));
		when(rmApi.v1RoomIdMembershipListGet(Mockito.anyString(), Mockito.isNull())).thenReturn(ml);
	
		Assertions.assertEquals(
			Collections.singletonList(new SymphonyUser(123l, "Roberto Banquet", "r@example.com")), 
					ruBuilder.getRoomMembers(out));
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
