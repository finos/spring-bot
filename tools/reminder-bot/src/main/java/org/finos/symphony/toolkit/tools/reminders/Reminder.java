/**
 * 
 */
package org.finos.symphony.toolkit.tools.reminders;

import java.time.LocalDateTime;

import org.finos.symphony.toolkit.workflow.annotations.Template;
import org.finos.symphony.toolkit.workflow.annotations.Work;
import org.finos.symphony.toolkit.workflow.content.User;
import org.finos.symphony.toolkit.workflow.sources.symphony.handlers.freemarker.annotations.Display;

/**
 * @author Gaurav Pancholi
 *
 */

@Work
@Template(
		edit = "create-reminder", 
		view="display-reminder")
public class Reminder {
	
	String description;
	
	@Display(name = "Remind At")
	LocalDateTime localTime;

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

	public Reminder(){

	}

}
