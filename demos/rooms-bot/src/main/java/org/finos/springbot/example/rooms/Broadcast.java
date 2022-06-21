package org.finos.springbot.example.rooms;

import org.finos.springbot.workflow.annotations.RequiresChatList;
import org.finos.springbot.workflow.annotations.Work;
import org.finos.springbot.workflow.content.Chat;

@Work
@RequiresChatList
public class Broadcast {

	String send;

	Chat to;

	public String getSend() {
		return send;
	}

	public void setSend(String send) {
		this.send = send;
	}

	public Chat getTo() {
		return to;
	}

	public void setTo(Chat to) {
		this.to = to;
	}

	
	
	
}
