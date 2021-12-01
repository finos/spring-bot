package org.finos.springbot.symphony.conversations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.finos.springbot.symphony.AbstractStreamResolving;
import org.finos.springbot.symphony.content.SymphonyRoom;
import org.finos.springbot.symphony.content.SymphonyUser;
import org.finos.springbot.workflow.content.Addressable;
import org.finos.springbot.workflow.content.Chat;
import org.finos.springbot.workflow.content.User;
import org.finos.symphony.toolkit.spring.api.properties.SymphonyApiProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import com.symphony.api.id.SymphonyIdentity;
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
import com.symphony.api.model.V3RoomSearchResults;
import com.symphony.api.pod.RoomMembershipApi;
import com.symphony.api.pod.StreamsApi;
import com.symphony.api.pod.UsersApi;
import com.symphony.user.StreamID;

/**
 * Basic implementation of symphony rooms with no caching.
 * @author Rob Moffat
 *
 */
public class SymphonyConversationsImpl extends AbstractStreamResolving implements SymphonyConversations, InitializingBean {
	
	private static final Logger LOG = LoggerFactory.getLogger(SymphonyConversationsImpl.class);

	
	private RoomMembershipApi rmApi;
	private SymphonyIdentity botIdentity;
	private List<User> defaultAdministrators = new ArrayList<User>();
	private long botUserId;
	
	public SymphonyConversationsImpl(RoomMembershipApi rmApi, StreamsApi streamsApi, UsersApi usersApi, SymphonyIdentity botIdentity, SymphonyApiProperties symphonyApiProperties) {
		super(streamsApi, usersApi, symphonyApiProperties);
		this.rmApi = rmApi;
		this.botIdentity = botIdentity;
	}
	
	
	@Override
	public Set<Addressable> getAllAddressables() {
		return getAllConversationsFiltered(new StreamFilter());
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Set<SymphonyRoom> getAllChats() {
		StreamType st = new StreamType().type(TypeEnum.ROOM);
		StreamFilter streamTypes = new StreamFilter().streamTypes(Collections.singletonList(st));
		return (Set<SymphonyRoom>) (Set) getAllConversationsFiltered(streamTypes);
	}

	protected Set<Addressable> getAllConversationsFiltered(StreamFilter f) {
		f.includeInactiveStreams(false);
		Set<Addressable> out = new HashSet<>();

		int skip = 0;
		
		StreamList sl;
		do {
			sl = streamsApi.v1StreamsListPost(null, f, skip, 50);
			sl.forEach(si -> {
				Addressable a  = convertToAddressable(si);
				if (a != null) {
					out.add(a);
				}
			});
			skip += sl.size();
		} while (sl.size() == 50);
		
		
		return out;
	}

	protected Addressable convertToAddressable(StreamAttributes si) {
		switch (si.getStreamType().getType()) {
			case "IM":
				return loadUserByStreamAttributes(si);
			case "MIM":
				// not supported yet
				return null;
			case "ROOM":
				return loadRoomByStreamAttributes(si);
			default: 
				return null;
		}

	}

	protected SymphonyUser loadUserByStreamAttributes(StreamAttributes si) {
		if (si.getStreamAttributes().getMembers().size() != 2) {
			return null;
		} else {
			Long userId = si.getStreamAttributes().getMembers().stream()
				.filter(id -> id != botUserId)
				.findFirst().orElse(null);
			
			if (userId == null) {
				return null;
			}
			
			SymphonyUser out = new SymphonyUser(userId);
			out.getId().add(new StreamID(si.getId()));
			return out;
		}
	}
	
	protected SymphonyRoom loadRoomByStreamAttributes(StreamAttributes si) {
		return new SymphonyRoom(si.getRoomAttributes().getName(), si.getId());
	}


	@Override
	public SymphonyUser loadUserById(Long userId) {
		UserV2 user = usersApi.v2UserGet(null, userId, null, null, symphonyApiProperties.isLocalPod());
		return new SymphonyUser(userId, user.getDisplayName(),user.getEmailAddress());
	}
	
	
	
	@Override
	public SymphonyUser loadUserByEmail(String name) {
		UserV2 user = usersApi.v2UserGet(null, null, name, null, symphonyApiProperties.isLocalPod());
		return new SymphonyUser(user.getId(), user.getDisplayName(),user.getEmailAddress());
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


	@Override
	public SymphonyRoom loadRoomByName(String name) {
		V2RoomSearchCriteria rsc = new V2RoomSearchCriteria();
		rsc.setQuery(name);
		V3RoomSearchResults res = streamsApi.v3RoomSearchPost(rsc, null, null, null);
		return res.getRooms().stream()
				.filter(r -> r.getRoomAttributes().getName().equals(name))
				.findFirst()
				.map(rd -> new SymphonyRoom(rd.getRoomAttributes().getName(), rd.getRoomSystemInfo().getId()))
				.orElse(null);
	}
	
	@Override
	public SymphonyRoom ensureChat(SymphonyRoom r, List<SymphonyUser> users, Map<String, Object> meta) {
		String description = "";
		String name = r.getName();
		boolean isPublic = false;
		
		description = (String) meta.getOrDefault(ROOM_DESCRIPTION, "");
		isPublic = (boolean) meta.getOrDefault(ROOM_PUBLIC, false);
		
		SymphonyRoom theRoom = null;
	
		if (r instanceof SymphonyRoom) {
			if (((SymphonyRoom) r).getKey() != null) {
				theRoom = (SymphonyRoom) r;
			} else {
				theRoom = loadRoomByName(name);
			} 
		}
		
		if (theRoom == null) {
			// create the room
			V3RoomAttributes ra = new V3RoomAttributes()
				.name(name)
				.description(description)
				._public(isPublic)
				.discoverable(isPublic);
			V3RoomDetail detail = streamsApi.v3RoomCreatePost(ra, null);
			String streamId = detail.getRoomSystemInfo().getId();
			
		
			theRoom = new SymphonyRoom(name, streamId);
			
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
			
			LOG.info("Created room {} with admins {} ", theRoom, getDefaultAdministrators());
		}
			
		// next, ensure that all the users are in the room
		
		String streamId = theRoom.getKey();
			
		users.stream()
			.filter(u -> u instanceof SymphonyUser)
			.map(u -> (SymphonyUser) u)
			.forEach(u -> rmApi.v1RoomIdMembershipAddPost(
				new UserId().id(Long.parseLong(u.getUserId())), null, streamId));
		
		return theRoom;
	}


	@Override
	public List<SymphonyUser> getChatMembers(SymphonyRoom r) {
		if (r instanceof SymphonyRoom) {
			MembershipList ml = rmApi.v2RoomIdMembershipListGet(((SymphonyRoom) r).getKey(), null);
			return ml.stream()
				.map(m -> loadUserById(m.getId()))
				.collect(Collectors.toList());	
		} else {
			return null;
		}
	}

	@Override
	public List<SymphonyUser> getChatAdmins(SymphonyRoom r) {
		if (r instanceof SymphonyRoom) {
			MembershipList ml = rmApi.v2RoomIdMembershipListGet(((SymphonyRoom) r).getKey(), null);
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


	@Override
	public void afterPropertiesSet() throws Exception {
		com.symphony.api.model.UserV2 u = usersApi.v2UserGet(null, null, botIdentity.getEmail(), null, symphonyApiProperties.isLocalPod());
		botUserId = u.getId();
	}


	@Override
	public SymphonyRoom getExistingChat(String name) {
		return loadRoomByName(name);
	}


	@Override
	public boolean isSupported(Chat r) {
		return r instanceof SymphonyRoom;
	}


	@Override
	public boolean isSupported(User u) {
		return u instanceof SymphonyUser;
	}
}
