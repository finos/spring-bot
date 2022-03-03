/**
 * 
 */
package org.finos.springbot.example.todo;

import org.finos.springbot.workflow.annotations.RequiresChatList;
import org.finos.springbot.workflow.annotations.RequiresUserList;
import org.finos.springbot.workflow.annotations.Work;
import org.finos.springbot.workflow.content.Chat;
import org.finos.springbot.workflow.content.User;

/**
 * @author rupnsur
 *
 */
@Work(index = false)
@RequiresUserList
@RequiresChatList
public class NewItemDetails {
	

	String description;
	User assignTo;
	Chat room;
	
	public Chat getRoom() {
		return room;
	}
	public void setRoom(Chat room) {
		this.room = room;
	}
	public NewItemDetails() {
		super();
	}
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	/**
	 * @return the assignTo
	 */
	public User getAssignTo() {
		return assignTo;
	}
	/**
	 * @param assignTo the assignTo to set
	 */
	public void setAssignTo(User assignTo) {
		this.assignTo = assignTo;
	}
	
}
