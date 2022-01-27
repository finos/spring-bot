/**
 * 
 */
package org.finos.springbot.examples.todo;

import org.finos.springbot.workflow.annotations.RequiresUserList;
import org.finos.springbot.workflow.annotations.Work;
import org.finos.springbot.workflow.content.User;

/**
 * @author rupnsur
 *
 */
@Work(index = false)
@RequiresUserList
public class NewItemDetails {
	

	String description;
	User assignTo;
	
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
