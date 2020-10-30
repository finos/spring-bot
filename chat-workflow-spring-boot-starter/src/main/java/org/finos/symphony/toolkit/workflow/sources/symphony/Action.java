package org.finos.symphony.toolkit.workflow.sources.symphony;

import org.finos.symphony.toolkit.workflow.content.Room;
import org.finos.symphony.toolkit.workflow.content.User;

public interface Action {

	public Room getRoom();
	
	public User getUser();
}
