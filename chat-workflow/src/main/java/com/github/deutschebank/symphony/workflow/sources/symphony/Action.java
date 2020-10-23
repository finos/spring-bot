package com.github.deutschebank.symphony.workflow.sources.symphony;

import com.github.deutschebank.symphony.workflow.content.Room;
import com.github.deutschebank.symphony.workflow.content.User;

public interface Action {

	public Room getRoom();
	
	public User getUser();
}
