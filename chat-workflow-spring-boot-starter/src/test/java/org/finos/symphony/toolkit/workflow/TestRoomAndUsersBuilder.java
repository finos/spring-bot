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
		
		// just returns a single real user
		when(usersApi.v3UsersGet(any(), any(), any(), any(), any(), any()))
			.then(a -> new V2UserList().users(Collections.singletonList(new UserV2().id(45l).displayName("Robert Moffat").emailAddress("rob@kite9.com"))));
		
		when(usersApi.v2UserGet(any(), any(), any(), any(), any()))
		.then(a -> new UserV2().id(45l).displayName("Robert Moffat").emailAddress("rob@kite9.com"));
	
		
		// one room
		when(streamsApi.v1StreamsListPost(any(), any(), any(), any()))
			.then(a -> {
				StreamList out = new StreamList();
				out.add(new StreamAttributes().id("123"));
				return out;
			});
		
		when(streamsApi.v2RoomIdInfoGet(eq("123"), any()))
			.then(a -> new V2RoomDetail().roomAttributes(new V2RoomAttributes()._public(true).name("Initial Room").description("Bogus")));
		
		when(streamsApi.v3RoomCreatePost(any(), isNull()))
			.then(a -> new V3RoomDetail()
					.roomSystemInfo(new RoomSystemInfo().id("456"))
					.roomAttributes(new V3RoomAttributes()._public(false).name("Some Test Room").description("Still Bogus")));
		
		when(streamsApi.v3RoomIdInfoGet(any(), any()))
			.then(a -> new V3RoomDetail().roomAttributes(new V3RoomAttributes().name("Some Room").description("Bogus Room")._public(true)));
		
		ruBuilder = new SymphonyRoomsImpl(wf, rmApi, streamsApi, usersApi);
		
	}
	
	@Test
	public void testCreateRoom() {
		when(streamsApi.v1StreamsListPost(Mockito.isNull(), Mockito.any(), Mockito.eq(0), Mockito.eq(0)))
			.thenAnswer(c -> {
				StreamList out = new StreamList();
				out.add(new StreamAttributes()
					.roomAttributes(new RoomSpecificStreamAttributes()
						.name("Some Test Room"))
					.id("abc123"));
				
				return out;
			});
		
		RoomDef rd = new RoomDef("Some Test Room", "Automated Test Room Created", true, null);
		Room out = ruBuilder.ensureRoom(rd);
		assertEquals("Some Test Room", out.getRoomName());
		assertEquals(1, ruBuilder.getAllRooms().size());
		
		String someStream = ruBuilder.getStreamFor(out);
		Assertions.assertEquals("abc123", someStream);
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
