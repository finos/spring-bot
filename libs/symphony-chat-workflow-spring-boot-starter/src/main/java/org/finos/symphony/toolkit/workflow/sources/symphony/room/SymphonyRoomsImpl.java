package org.finos.symphony.toolkit.workflow.sources.symphony.room;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.finos.symphony.toolkit.workflow.content.Chat;
import org.finos.symphony.toolkit.workflow.content.User;
import org.finos.symphony.toolkit.workflow.sources.symphony.content.SymphonyRoom;
import org.finos.symphony.toolkit.workflow.sources.symphony.content.SymphonyUser;
import org.finos.symphony.toolkit.workflow.sources.symphony.streams.AbstractStreamResolving;

import com.symphony.api.model.MembershipList;
import com.symphony.api.model.StreamAttributes;
import com.symphony.api.model.StreamFilter;
import com.symphony.api.model.StreamList;
import com.symphony.api.model.StreamType;
import com.symphony.api.model.StreamType.TypeEnum;
import com.symphony.api.model.UserId;
import com.symphony.api.model.UserV2;
import com.symphony.api.model.V2RoomSearchCriteria;
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
public class SymphonyRoomsImpl extends AbstractStreamResolving implements SymphonyRooms {
	
	private RoomMembershipApi rmApi;
	private List<User> defaultAdministrators = new ArrayList<User>();
	
	public SymphonyRoomsImpl(RoomMembershipApi rmApi, StreamsApi streamsApi, UsersApi usersApi) {
		super(streamsApi, usersApi);
		this.rmApi = rmApi;
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
	public SymphonyUser loadUserById(Long userId) {
		UserV2 user = usersApi.v2UserGet(null, userId, null, null, true);
		return new SymphonyUser(userId.toString(), user.getDisplayName(),user.getEmailAddress());
	}

	@Override
	public SymphonyRoom loadRoomById(String streamId) {
		try {
			V3RoomDetail r = streamsApi.v3RoomIdInfoGet(streamId, null);
			return new SymphonyRoom(r.getRoomAttributes().getName(), streamId);
		} catch (Exception e) {
			return null;
		}
	}


	public Chat doesRoomExist(String name) {
		V2RoomSearchCriteria rsc = new V2RoomSearchCriteria();
		rsc.setQuery(name);
		streamsApi.v3RoomSearchPost(rsc, null, null, null)
		
		
	}
	
	@Override
	public Chat ensureRoom(Chat r, List<User> users, Map<String, Object> meta) {
		String description = "";
		String name = r.getName();
		boolean isPublic = false;
		
		description = (String) meta.getOrDefault(ROOM_DESCRIPTION, "");
		isPublic = (boolean) meta.getOrDefault(ROOM_PUBLIC, false);
		
		String streamId;
	
		if (r instanceof SymphonyRoom) {
			if (((SymphonyRoom) r).getStreamId() == null) {
				// new room neede
			}
			// create the room
			V3RoomAttributes ra = new V3RoomAttributes()
				.name(name)
				.description(description)
				._public(isPublic)
				.discoverable(isPublic);
			V3RoomDetail detail = streamsApi.v3RoomCreatePost(ra, null);
			String streamId = detail.getRoomSystemInfo().getId();
			
			// next, we need to make sure that all of the admins are members of the room and owners.
			List<Long> adminIds = getDefaultAdministrators().stream()
				.filter(u -> u instanceof SymphonyUser)
				.map(u -> (SymphonyUser) u)
				.map(su -> Long.parseLong(su.getUserId()))
				.filter(id -> id != null)
				.collect(Collectors.toList());
			
			for (Long user : adminIds) {
				UserId u = new UserId().id(user);
				rmApi.v1RoomIdMembershipAddPost(u, null, streamId);
				rmApi.v1RoomIdMembershipPromoteOwnerPost(u, null, streamId);
			}
		}
		
		for (User u : users) {
			
		}
	
		return new SymphonyRoom(
				detail.getRoomAttributes().getName(), 
				streamId);
	}


	@Override
	public List<User> getRoomMembers(Chat r) {
		if (r instanceof SymphonyRoom) {
			MembershipList ml = rmApi.v1RoomIdMembershipListGet(((SymphonyRoom) r).getStreamId(), null);
			return ml.stream()
				.map(m -> loadUserById(m.getId()))
				.collect(Collectors.toList());	
		} else {
			return null;
		}
	}

	@Override
	public List<User> getRoomAdmins(Chat r) {
		if (r instanceof SymphonyRoom) {
			MembershipList ml = rmApi.v1RoomIdMembershipListGet(((SymphonyRoom) r).getStreamId(), null);
			return ml.stream()
				.filter(m -> m.isOwner())
				.map(m -> loadUserById(m.getId()))
				.collect(Collectors.toList());
		} else {
			return null;
		}
	}


	public List<User> getDefaultAdministrators() {
		return defaultAdministrators;
	}

	public void setDefaultAdministrators(List<User> defaultAdministrators) {
		this.defaultAdministrators = defaultAdministrators;
	}
}
