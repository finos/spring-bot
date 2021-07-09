package org.finos.symphony.toolkit.workflow.sources.symphony.room;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.finos.symphony.toolkit.workflow.content.Addressable;
import org.finos.symphony.toolkit.workflow.content.Chat;
import org.finos.symphony.toolkit.workflow.content.User;
import org.finos.symphony.toolkit.workflow.sources.symphony.content.SymphonyRoom;
import org.finos.symphony.toolkit.workflow.sources.symphony.content.SymphonyUser;

import com.symphony.api.model.MembershipList;
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
public class SymphonyRoomsImpl implements SymphonyRooms {
	
	private RoomMembershipApi rmApi;
	private StreamsApi streamsApi;
	private UsersApi usersApi;
	
	public SymphonyRoomsImpl(RoomMembershipApi rmApi, StreamsApi streamsApi, UsersApi usersApi) {
		this.rmApi = rmApi;
		this.streamsApi = streamsApi;
		this.usersApi = usersApi;
	}
	
	@Override
	public Set<Chat> getAllRooms() {
		StreamType st = new StreamType().type(TypeEnum.ROOM);
		StreamList list = streamsApi.v1StreamsListPost(null, new StreamFilter().streamTypes(Collections.singletonList(st)), 0, 0);
		Set<Chat> out = new HashSet<>();
		for (StreamAttributes streamAttributes : list) {
			Chat r = loadRoomById(streamAttributes.getId());
			out.add(r);
		}
		
		return out;
	}

	@Override
	public User loadUserById(Long userId) {
		UserV2 user = usersApi.v2UserGet(null, userId, null, null, true);
		return new SymphonyUser(userId.toString(), user.getDisplayName(),user.getEmailAddress());
	}

	@Override
	public Chat loadRoomById(String streamId) {
		try {
			V3RoomDetail r = streamsApi.v3RoomIdInfoGet(streamId, null);
			return new SymphonyRoom(r.getRoomAttributes().getName(), r.getRoomAttributes().getDescription(), r.getRoomAttributes().isPublic(), streamId);
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public String getStreamFor(Addressable a) {
		if (a instanceof SymphonyUser) {
			return ((SymphonyUser) a).getStreamId()
		} else if (a instanceof SymphonyRoom) {
			if (((SymphonyRoom) a).getId() != null) {
				return ((SymphonyRoom) a).getId();
			}
			
			StreamType st = new StreamType().type(TypeEnum.ROOM);
			StreamList list = streamsApi.v1StreamsListPost(null, new StreamFilter().streamTypes(Collections.singletonList(st)), 0, 0);
			Map<Chat, String> out = new HashMap<>();
			for (StreamAttributes streamAttributes : list) {
				if (streamAttributes.getRoomAttributes().getName().equals(((Chat) a).getName())) {
					return streamAttributes.getId();
				}
			}
			
			// ok, need to create the room
			return ensureRoom((Chat) a).getId();
		} else {
			throw new UnsupportedOperationException("What is this? "+a);
		}
	}
	
	public Chat ensureRoom(Chat r) {
		// create the room
		V3RoomAttributes ra = new V3RoomAttributes()
			.name(r.getName())
			.description(r.getRoomDescription())
			._public(r.isPub())
			.discoverable(r.isPub());
		V3RoomDetail detail = streamsApi.v3RoomCreatePost(ra, null);
		
		// next, we need to make sure that all of the admins are members of the room and owners.
		List<Long> adminIds = wf.getAdministrators().stream()
			.map(admin -> Long.parseLong(admin.getId()))
			.collect(Collectors.toList());
		
		for (Long user : adminIds) {
			UserId u = new UserId().id(user);
			rmApi.v1RoomIdMembershipAddPost(u, null, detail.getRoomSystemInfo().getId());
			rmApi.v1RoomIdMembershipPromoteOwnerPost(u, null, detail.getRoomSystemInfo().getId());
		}
		
		return new SymphonyRoom(
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
			UserV2 user = usersApi.v2UserGet(null, null, u.getEmailAddress(), null, true);
			return user.getId();
		}
	}

	@Override
	public List<User> getRoomMembers(Chat r) {
		MembershipList ml = rmApi.v1RoomIdMembershipListGet(r.getId(), null);
		return ml.stream()
			.map(m -> loadUserById(m.getId()))
			.collect(Collectors.toList());
	}

	@Override
	public List<User> getRoomAdmins(Chat r) {
		MembershipList ml = rmApi.v1RoomIdMembershipListGet(r.getId(), null);
		return ml.stream()
			.filter(m -> m.isOwner())
			.map(m -> loadUserById(m.getId()))
			.collect(Collectors.toList());
	}

}
