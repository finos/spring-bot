/**
 * 
 */
package org.finos.symphony.toolkit.tools.reminders;

import org.finos.symphony.toolkit.workflow.java.Work;
import org.finos.symphony.toolkit.workflow.sources.symphony.Template;

import java.time.Instant;

/**
 * @author Gaurav Pancholi
 *
 */

@Work(name = "Create Reminder", instructions = "Add the new Reminder")
@Template(view = "classpath:/create-reminder.ftl")
public class Reminder {
	
	String description;
	Instant instant;
	String author;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Instant getInstant() {
		return instant;
	}

	public void setInstant(Instant instant) {
		this.instant = instant;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}
	public Reminder(){

	}

}
