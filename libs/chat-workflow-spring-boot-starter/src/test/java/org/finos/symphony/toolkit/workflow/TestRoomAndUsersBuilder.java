package org.finos.symphony.toolkit.workflow;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.finos.symphony.toolkit.workflow.content.Room;
import org.finos.symphony.toolkit.workflow.content.RoomDef;
import org.finos.symphony.toolkit.workflow.content.UserDef;
import org.finos.symphony.toolkit.workflow.fixture.TestWorkflowConfig;
import org.finos.symphony.toolkit.workflow.sources.symphony.room.SymphonyRooms;
import org.finos.symphony.toolkit.workflow.sources.symphony.room.SymphonyRoomsImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.symphony.api.model.MemberInfo;
import com.symphony.api.model.MembershipList;
import com.symphony.api.model.RoomSpecificStreamAttributes;
import com.symphony.api.model.RoomSystemInfo;
import com.symphony.api.model.Stream;
import com.symphony.api.model.StreamAttributes;
import com.symphony.api.model.StreamList;
import com.symphony.api.model.UserV2;
import com.symphony.api.model.V2RoomAttributes;
import com.symphony.api.model.V2RoomDetail;
import com.symphony.api.model.V2UserList;
import com.symphony.api.model.V3RoomAttributes;
import com.symphony.api.model.V3RoomDetail;
import com.symphony.api.pod.RoomMembershipApi;
import com.symphony.api.pod.StreamsApi;
import com.symphony.api.pod.UsersApi;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {  TestWorkflowConfig.class })
public class TestRoomAndUsersBuilder {
	
	SymphonyRooms ruBuilder;
	
	@MockBean
	RoomMembershipApi rmApi;
	
	@MockBean
	StreamsApi streamsApi;
	
	@MockBean
	UsersApi usersApi;
	
	@Autowired
	Workflow wf;
	
	@BeforeEach
	public void setup() {

		ruBuilder = new SymphonyRoomsImpl(wf, rmApi, streamsApi, usersApi);
		
	}
	
	@Test
	public void testCreateRoom() {
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
	
		
		RoomDef rd = new RoomDef("Some Test Room", "Automated Test Room Created", true, null);
		Room out = ruBuilder.ensureRoom(rd);
		assertEquals("Some Test Room", out.getRoomName());
		assertEquals(1, ruBuilder.getAllRooms().size());
		
		String someStream = ruBuilder.getStreamFor(out);
		Assertions.assertEquals("456", someStream);

		// return members
		MembershipList ml = new MembershipList();
		ml.add(new MemberInfo().id(123l).owner(true));
		when(rmApi.v1RoomIdMembershipListGet(Mockito.anyString(), Mockito.isNull())).thenReturn(ml);

		when(usersApi.v2UserGet(any(), any(), any(), any(), any()))
			.then(a -> new UserV2().id(45l).displayName("Roberto Banquet").emailAddress("r@example.com"));
	
		Assertions.assertEquals(
			Collections.singletonList(new UserDef("123", "Roberto Banquet", "r@example.com")), 
					ruBuilder.getRoomMembers(out));
	}
	
	@Test
	public void testGetUserStream() {
		when(streamsApi.v1ImCreatePost(Mockito.any(),Mockito.isNull()))
			.thenAnswer(c -> new Stream().id("123"));
		
		UserDef rd = new UserDef("123", "Robski mo", "rob@example.com");
		String someStream = ruBuilder.getStreamFor(rd);
		Assertions.assertEquals("123", someStream);
	}
}
