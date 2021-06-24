package org.finos.symphony.toolkit.tools.reminders;

import org.finos.symphony.toolkit.workflow.content.Addressable;
import org.finos.symphony.toolkit.workflow.content.Author;
import org.finos.symphony.toolkit.workflow.history.History;
import org.finos.symphony.toolkit.workflow.java.Exposed;
import org.finos.symphony.toolkit.workflow.java.Work;
import org.finos.symphony.toolkit.workflow.sources.symphony.Template;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;

/**
 * @author Gaurav Pancholi
 *
 */

@Work(name = "Reminder List", instructions = "List of Reminders to be conveyed", editable = true)
@Template(edit = "classpath:/edit-reminder-list.ftl")
public class ReminderList {

	List<Reminder> reminders = new ArrayList<>();

	TimeZone timeZone;

	public TimeZone getTimeZone() {
		return timeZone;
	}

	public void setTimeZone(TimeZone timeZone) {
		this.timeZone = timeZone;
	}

	public List<Reminder> getReminders() {
		return reminders;
	}

	public void setReminders(List<Reminder> reminders) {
		this.reminders = reminders;
	}

	@Exposed(description = "Add an Reminder", addToHelp = false)
	public static ReminderList addreminder(Reminder cr, History h, Addressable a, Author author, ReminderProperties rp) {
		ReminderList rl = listReminders(h, author, rp);
		cr.setAuthor(author.getName());
		System.out.println(cr.getDescription());
		rl.reminders.add(cr);
		return rl;
	
	}

	@Exposed(description = "Show list of Reminders", isMessage = true)
	public static ReminderList listReminders(History h, Addressable a, ReminderProperties rp) {
		Optional<ReminderList> rl = h.getLastFromHistory(ReminderList.class, a);

		if (rl.isPresent()) {
			return rl.get();
		} else {
			ReminderList out = new ReminderList();
			out.setTimeZone(rp.getDefaultTimeZone());
			return out;
		}

	}
}
