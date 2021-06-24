package org.finos.symphony.toolkit.tools.reminders;

import org.finos.symphony.toolkit.workflow.content.Addressable;
import org.finos.symphony.toolkit.workflow.history.History;
import org.finos.symphony.toolkit.workflow.java.Exposed;
import org.finos.symphony.toolkit.workflow.java.Work;
import org.finos.symphony.toolkit.workflow.sources.symphony.Template;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author Gaurav Pancholi
 *
 */

@Work(name = "Reminder List", instructions = "List of Reminders to be conveyed", editable = true)
//@Template(view = "classpath:/create-reminder.ftl")
@Template(edit="classpath:/delete-reminder.ftl")
public class ReminderList {

	List<Reminder> reminders = new ArrayList<>();

	public List<Reminder> getReminders() {
		return reminders;
	}

	public void setReminders(List<Reminder> reminders) {
		this.reminders = reminders;
	}

	@Exposed(description = "Add an Reminder",addToHelp = false)
	public static ReminderList addreminder(Reminder cr, History h, Addressable a) {
		Optional<ReminderList> rl =h.getLastFromHistory(ReminderList.class,a);

		if(rl.isPresent()){
			//rl.get().remList.clear();
			System.out.println(cr.getDescription());
			rl.get().reminders.add(cr);
			return rl.get();
		   }
		    else
		   {
			ReminderList out = new ReminderList();
			out.reminders.add(cr);
			return out;
		}
	}

	@Exposed(description = "Show list of Reminders", isMessage = true)
	public static ReminderList listReminders(History h,Addressable a){
		Optional<ReminderList> rl =h.getLastFromHistory(ReminderList.class,a);
		if(rl.isPresent()){
         return rl.get();
		}
		else{
			return new ReminderList();
		}

	}
}

