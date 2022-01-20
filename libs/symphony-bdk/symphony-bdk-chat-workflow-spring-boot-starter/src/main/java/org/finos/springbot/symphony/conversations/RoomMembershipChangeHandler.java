package org.finos.springbot.symphony.conversations;

import java.util.Collections;
import java.util.List;

import org.finos.springbot.symphony.content.SymphonyRoom;
import org.finos.springbot.symphony.content.SymphonyUser;
import org.finos.springbot.workflow.actions.Action;
import org.finos.springbot.workflow.actions.MemberAction;
import org.finos.springbot.workflow.actions.consumers.ActionConsumer;
import org.springframework.context.event.EventListener;

import com.symphony.bdk.gen.api.model.V4RoomCreated;
import com.symphony.bdk.gen.api.model.V4UserJoinedRoom;
import com.symphony.bdk.gen.api.model.V4UserLeftRoom;
import com.symphony.bdk.spring.events.RealTimeEvent;

public class RoomMembershipChangeHandler {

	protected final List<ActionConsumer> messageConsumers;
	protected final SymphonyConversations sc;
	
	public RoomMembershipChangeHandler(List<ActionConsumer> messageConsumers, SymphonyConversations sc) {
		super();
		this.messageConsumers = messageConsumers;
		this.sc = sc;
	}

	@EventListener
	public void created(RealTimeEvent<V4RoomCreated> event) {
		V4RoomCreated roomCreated = event.getSource();
		SymphonyRoom room = sc.loadRoomById(roomCreated.getStream().getStreamId());
		SymphonyUser user = sc.loadUserById(roomCreated.getRoomProperties().getCreatorUser().getUserId());
		MemberAction a = new MemberAction(room, user, MemberAction.Type.ADDED, Collections.emptyList());
		post(a);
	}

	private void post(MemberAction a) {
		try {
			Action.CURRENT_ACTION.set(a);
			for (ActionConsumer c : messageConsumers) {
				c.accept(a);
			}
		} finally {
			Action.CURRENT_ACTION.set(Action.NULL_ACTION);
		}
	}
	
	@EventListener
	public void joined(RealTimeEvent<V4UserJoinedRoom> event) {
		V4UserJoinedRoom userJoined = event.getSource();
		SymphonyRoom room = sc.loadRoomById(userJoined.getStream().getStreamId());
		SymphonyUser user = sc.loadUserById(userJoined.getAffectedUser().getUserId());
		MemberAction a = new MemberAction(room, user, MemberAction.Type.ADDED, Collections.emptyList());
		post(a);
	}
	
	@EventListener
	public void left(RealTimeEvent<V4UserLeftRoom> event) {
		V4UserLeftRoom userLeft = event.getSource();
		SymphonyRoom room = sc.loadRoomById(userLeft.getStream().getStreamId());
		SymphonyUser user = sc.loadUserById(userLeft.getAffectedUser().getUserId());
		MemberAction a = new MemberAction(room, user, MemberAction.Type.REMOVED, Collections.emptyList());
		post(a);
	}
}
