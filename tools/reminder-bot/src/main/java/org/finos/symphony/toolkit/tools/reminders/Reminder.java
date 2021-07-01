/**
 * 
 */
package org.finos.symphony.toolkit.tools.reminders;

import org.finos.symphony.toolkit.workflow.content.User;
import org.finos.symphony.toolkit.workflow.java.Work;
import org.finos.symphony.toolkit.workflow.sources.symphony.Template;
import org.finos.symphony.toolkit.workflow.sources.symphony.handlers.freemarker.annotations.Display;

import java.time.Instant;
import java.time.LocalDateTime;

/**
 * @author Gaurav Pancholi
 *
 */

@Work(name = "Create Reminder", instructions = "Add the new Reminder")
@Template(edit = "classpath:/create-reminder.ftl", view="classpath:/display-reminder.ftl")
public class Reminder {
	
	String description;
	
	@Display(name = "Remind At")
	LocalDateTime localTime;

	User author;

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
