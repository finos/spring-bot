package org.finos.springbot.example.todo;

import org.finos.springbot.workflow.annotations.RequiresChatList;
import org.finos.springbot.workflow.annotations.Work;
import org.finos.springbot.workflow.content.Chat;

@Work
@RequiresChatList
public class SendToRoom {

	Chat room;

	public Chat getRoom() {
		return room;
	}

	public void setRoom(Chat room) {
		this.room = room;
	}
	
	
}
