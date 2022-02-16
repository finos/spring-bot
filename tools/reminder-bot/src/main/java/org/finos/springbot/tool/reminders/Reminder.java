/**
 * 
 */
package org.finos.springbot.tool.reminders;

import java.time.LocalDateTime;

import org.finos.springbot.workflow.actions.Action;
import org.finos.springbot.workflow.annotations.Display;
import org.finos.springbot.workflow.annotations.RequiresUserList;
import org.finos.springbot.workflow.annotations.Template;
import org.finos.springbot.workflow.annotations.Work;
import org.finos.springbot.workflow.content.User;

/**
 * @author Gaurav Pancholi
 *
 */

@Work
@Template(edit = "create-reminder", view="display-reminder")
@RequiresUserList
public class Reminder {
	
	String description;
	
	@Display(name = "Remind At")
	LocalDateTime localTime;

	User author = Action.CURRENT_ACTION.get().getUser();

	public LocalDateTime getLocalTime() {
		return localTime;
	}

	public void setLocalTime(LocalDateTime localTime) {
		this.localTime = localTime;
	}
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public User getAuthor() {
		return author;
	}

	public void setAuthor(User author) {
		this.author = author;
	}

	public Reminder(){

	}

}
