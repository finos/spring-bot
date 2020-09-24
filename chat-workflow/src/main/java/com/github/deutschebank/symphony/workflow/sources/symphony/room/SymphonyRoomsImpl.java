package com.github.deutschebank.symphony.workflow.sources.symphony.room;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.github.deutschebank.symphony.workflow.AbstractNeedsWorkflow;
import com.github.deutschebank.symphony.workflow.Workflow;
import com.github.deutschebank.symphony.workflow.content.Addressable;
import com.github.deutschebank.symphony.workflow.content.Room;
import com.github.deutschebank.symphony.workflow.content.RoomDef;
import com.github.deutschebank.symphony.workflow.content.User;
import com.github.deutschebank.symphony.workflow.content.UserDef;
import com.symphony.api.model.StreamAttributes;
import com.symphony.api.model.StreamFilter;
import com.symphony.api.model.StreamList;
import com.symphony.api.model.StreamType;
import com.symphony.api.model.StreamType.TypeEnum;
import com.symphony.api.model.UserId;
import com.symphony.api.model.UserV2;
import com.symphony.api.model.V3RoomAttributes;
import com.symphony.api.model.V3RoomDetail;
import com.symphony.api.pod.RoomMembershipApi;
import com.symphony.api.pod.StreamsApi;
import com.symphony.api.pod.UsersApi;

/**
 * Basic implementation of symphony rooms with no caching.
 * @author Rob Moffat
 *
 */
public class SymphonyRoomsImpl extends AbstractNeedsWorkflow implements SymphonyRooms {
	
	private RoomMembershipApi rmApi;
	private StreamsApi streamsApi;
	private UsersApi usersApi;
	private List<Long> adminUserIds = Collections.emptyList();
	
	public SymphonyRoomsImpl(Workflow wf, RoomMembershipApi rmApi, StreamsApi streamsApi, UsersApi usersApi) {
		super(wf);
		wf.registerRoomsProvider(this);
		this.rmApi = rmApi;
		this.streamsApi = streamsApi;
		this.usersApi = usersApi;
		this.adminUserIds = wf.getAdministrators().stream()
				.map(u -> getId(u))
				.collect(Collectors.toList());
	}
	
	@Override
	public Set<Room> getAllRooms() {
		StreamType st = new StreamType().type(TypeEnum.ROOM);
		StreamList list = streamsApi.v1StreamsListPost(null, new StreamFilter().streamTypes(Collections.singletonList(st)), 0, 0);
		Set<Room> out = new HashSet<>();
		for (StreamAttributes streamAttributes : list) {
			Room r = loadRoomById(streamAttributes.getId());
			out.add(r);
		}
		
		return out;
	}

	@Override
	public User loadUserById(Long userId) {
		UserV2 user = usersApi.v2UserGet(null, userId, null, null, true);
		return new UserDef(userId.toString(), user.getDisplayName(),user.getEmailAddress());
	}

	@Override
	public Room loadRoomById(String streamId) {
		try {
			V3RoomDetail r = streamsApi.v3RoomIdInfoGet(streamId, null);
			return new RoomDef(r.getRoomAttributes().getName(), r.getRoomAttributes().getDescription(), r.getRoomAttributes().isPublic(), streamId);
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public String getStreamFor(Addressable a) {
		if (a instanceof User) {
			return streamsApi.v1ImCreatePost(Collections.singletonList(getId((User) a)), null).getId();
		} else if (a instanceof Room) {
			StreamType st = new StreamType().type(TypeEnum.ROOM);
			StreamList list = streamsApi.v1StreamsListPost(null, new StreamFilter().streamTypes(Collections.singletonList(st)), 0, 0);
			Map<Room, String> out = new HashMap<>();
			for (StreamAttributes streamAttributes : list) {
				if (streamAttributes.getRoomAttributes().getName().equals(((Room) a).getRoomName())) {
					return streamAttributes.getId();
				}
			}
			
			// ok, need to create the room
			return ensureRoom((Room) a).getId();
		} else {
			throw new UnsupportedOperationException("What is this? "+a);
		}
	}
	
	public Room ensureRoom(Room r) {
		// create the room
		V3RoomAttributes ra = new V3RoomAttributes()
			.name(r.getRoomName())
			.description(r.getRoomDescription())
			._public(r.isPub())
			.discoverable(r.isPub());
		V3RoomDetail detail = streamsApi.v3RoomCreatePost(ra, null);
		
		// next, we need to make sure that all of the admins are members of the room and owners.
		for (Long user : adminUserIds) {
			UserId u = new UserId().id(user);
			rmApi.v1RoomIdMembershipAddPost(u, null, detail.getRoomSystemInfo().getId());
			rmApi.v1RoomIdMembershipPromoteOwnerPost(u, null, detail.getRoomSystemInfo().getId());
		}
		
		return new RoomDef(
				detail.getRoomAttributes().getName(), 
				detail.getRoomAttributes().getDescription(),
				detail.getRoomAttributes().isPublic(),
				detail.getRoomSystemInfo().getId());
	}

	@Override
	public Long getId(User u) {
		if (u == null) {
			return null;
		} else if (u.getId() != null) {
			return Long.parseLong(u.getId());
		} else {
			UserV2 user = usersApi.v2UserGet(null, null, u.getAddress(), null, true);
			return user.getId();
		}
	}



}
