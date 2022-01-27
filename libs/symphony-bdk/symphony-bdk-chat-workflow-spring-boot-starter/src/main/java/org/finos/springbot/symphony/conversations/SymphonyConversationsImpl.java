package org.finos.springbot.symphony.conversations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.finos.springbot.symphony.SymphonyException;
import org.finos.springbot.symphony.content.SymphonyAddressable;
import org.finos.springbot.symphony.content.SymphonyRoom;
import org.finos.springbot.symphony.content.SymphonyUser;
import org.finos.springbot.workflow.content.Addressable;
import org.finos.springbot.workflow.content.Chat;
import org.finos.springbot.workflow.content.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import com.symphony.bdk.core.service.pagination.model.PaginationAttribute;
import com.symphony.bdk.core.service.session.SessionService;
import com.symphony.bdk.core.service.stream.StreamService;
import com.symphony.bdk.core.service.user.UserService;
import com.symphony.bdk.gen.api.model.MemberInfo;
import com.symphony.bdk.gen.api.model.StreamAttributes;
import com.symphony.bdk.gen.api.model.StreamFilter;
import com.symphony.bdk.gen.api.model.StreamType;
import com.symphony.bdk.gen.api.model.StreamType.TypeEnum;
import com.symphony.bdk.gen.api.model.UserV2;
import com.symphony.bdk.gen.api.model.V2RoomSearchCriteria;
import com.symphony.bdk.gen.api.model.V3RoomAttributes;
import com.symphony.bdk.gen.api.model.V3RoomDetail;
import com.symphony.bdk.gen.api.model.V3RoomSearchResults;
import com.symphony.user.StreamID;

/**
 * Basic implementation of symphony rooms with no caching.
 * @author Rob Moffat
 *
 */
public class SymphonyConversationsImpl implements SymphonyConversations, InitializingBean {
	
	private static final Logger LOG = LoggerFactory.getLogger(SymphonyConversationsImpl.class);

	private List<User> defaultAdministrators = new ArrayList<User>();
	private long botUserId;
	private final UserService userService;
	private final StreamService streamsApi;
	private final SessionService sessionService;
	private final boolean localPodLookup; 
	
	public SymphonyConversationsImpl(
			StreamService streamsApi, 
			UserService userService,
			SessionService sessionService,
			boolean localPodLookup) {
		this.userService = userService;
		this.streamsApi = streamsApi;
		this.localPodLookup = localPodLookup;
		this.sessionService = sessionService;
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
		
		List<StreamAttributes> sl;
		do {
			sl = streamsApi.listStreams(f, new PaginationAttribute(skip, 50));
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
			case IM:
				return loadUserByStreamAttributes(si);
			case MIM:
				// not supported yet
				return null;
			case ROOM:
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
		List<UserV2> users = userService.listUsersByIds(Collections.singletonList(userId), localPodLookup, true);
		if (users.size() == 1) {
			UserV2 user = users.get(0);
			return new SymphonyUser(userId, user.getDisplayName(),user.getEmailAddress());
		} else {
			throw new SymphonyException("User not found: "+userId);
		}
	}
	
	@Override
	public String getStreamFor(SymphonyAddressable a) {
		if (a instanceof SymphonyUser) {
			return getStreamIdForUser((SymphonyUser) a);
		} else if (a instanceof SymphonyRoom) {
			return ((SymphonyRoom) a).getKey();
		} else {
			throw new SymphonyException("What is this? "+a);
		}
	}

	protected String getStreamIdForUser(SymphonyUser a) {
		if (((SymphonyUser) a).getStreamId() != null) {
			return ((SymphonyUser) a).getStreamId();
		} else {
			if (a.getUserId() == null) {
				a = loadUserByEmail(a.getEmailAddress());
			}
			
			long userId = Long.parseLong(a.getUserId());
			com.symphony.bdk.gen.api.model.Stream s = streamsApi.create(Collections.singletonList(userId));
			a.getId().add(new StreamID(s.getId()));
			return s.getId();
		}
	}

	
	@Override
	public SymphonyUser loadUserByUsername(String username) {
		List<UserV2> users = userService.listUsersByUsernames(Collections.singletonList(username));
		if (users.size() == 1) {
			UserV2 user = users.get(0);
			return new SymphonyUser(user.getId(), user.getDisplayName(),user.getEmailAddress());
		} else {
			throw new SymphonyException("User not found: "+username);
		}
	}
	
	@Override
	public SymphonyUser loadUserByEmail(String name) {
		List<UserV2> users = userService.listUsersByEmails(Collections.singletonList(name), localPodLookup, true);
		if (users.size() == 1) {
			UserV2 user = users.get(0);
			return new SymphonyUser(user.getId(), user.getDisplayName(),user.getEmailAddress());
		} else {
			throw new SymphonyException("User not found: "+name);
		}
	}

	@Override
	public SymphonyRoom loadRoomById(String streamId) {
		try {
			V3RoomDetail r = streamsApi.getRoomInfo(streamId);
			return new SymphonyRoom(r.getRoomAttributes().getName(), streamId);
		} catch (Exception e) {
			return null;
		}
	}


	@Override
	public SymphonyRoom loadRoomByName(String name) {
		V2RoomSearchCriteria rsc = new V2RoomSearchCriteria();
		rsc.setQuery(name);
		V3RoomSearchResults res = streamsApi.searchRooms(rsc);
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
			V3RoomDetail detail = streamsApi.create(ra);
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
				streamsApi.addMemberToRoom(user, streamId);
				streamsApi.promoteUserToRoomOwner(user, streamId);
			}
			
			LOG.info("Created room {} with admins {} ", theRoom, getDefaultAdministrators());
		}
			
		// next, ensure that all the users are in the room
		
		String streamId = theRoom.getKey();
			
		users.stream()
			.filter(u -> u instanceof SymphonyUser)
			.map(u -> (SymphonyUser) u)
			.forEach(u -> streamsApi.addMemberToRoom(Long.parseLong(u.getUserId()), streamId));
		
		return theRoom;
	}


	@Override
	public List<SymphonyUser> getChatMembers(SymphonyRoom r) {
		return getUsersInternal(r, f -> true);
	}

	@Override
	public List<SymphonyUser> getChatAdmins(SymphonyRoom r) {
		return getUsersInternal(r, f -> f.getOwner());
	}


	protected List<SymphonyUser> getUsersInternal(SymphonyRoom r, Predicate<MemberInfo> filter) {
		if (r instanceof SymphonyRoom) {
			List<MemberInfo> ml = streamsApi.listRoomMembers(r.getKey());
			List<Long> ids = ml.stream()
					.filter(filter)
					.map(e -> e.getId()).collect(Collectors.toList());
			
			List<UserV2> users = userService.listUsersByIds(ids, localPodLookup, true);

			return users.stream()
				.map(e -> new SymphonyUser(e.getId(), e.getDisplayName(), e.getEmailAddress()))
				.collect(Collectors.toList());	
		} else {
			throw new SymphonyException("Not a room: "+r);
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
		botUserId = sessionService.getSession().getId();	
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


	@Override
	public boolean isThisBot(User u) {
		if (u instanceof SymphonyUser) {
			return ((SymphonyUser) u).getUserId().equals(""+botUserId);
		} else {
			return false;
		}
	}
}
