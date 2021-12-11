package org.finos.symphony.toolkit.tools.reminders;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import org.finos.symphony.toolkit.workflow.annotations.Template;
import org.finos.symphony.toolkit.workflow.annotations.Work;

/**
 * @author Gaurav Pancholi
 *
 */

@Work
@Template(edit = "edit-reminder-list")
public class ReminderList {

	public ReminderList() {
		super();
	}

	public ReminderList(ReminderList old) {
		super();
		reminders = new ArrayList<>(old.getReminders());
		timeZone = old.timeZone;
		remindBefore = old.remindBefore;
	}

	
	List<Reminder> reminders = new ArrayList<>();

	ZoneId timeZone;

	Integer remindBefore;

	public ZoneId getTimeZone() {
		return timeZone;
	}

	public void setTimeZone(ZoneId timeZone) {
		this.timeZone = timeZone;
	}


	public int getRemindBefore() {
		return remindBefore;
	}

	public void setRemindBefore(int remindBefore) {
		this.remindBefore = remindBefore;
	}

	public List<Reminder> getReminders() {
		return reminders;
	}

	public void setReminders(List<Reminder> reminders) {
		this.reminders = reminders;
	}

	

}
