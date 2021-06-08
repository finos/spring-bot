/**
 * 
 */
package org.finos.symphony.toolkit.tools.reminders;

import org.finos.symphony.toolkit.tools.reminders.alerter.TimedAlerter;
import org.finos.symphony.toolkit.workflow.content.Addressable;
import org.finos.symphony.toolkit.workflow.history.History;
import org.finos.symphony.toolkit.workflow.java.Exposed;
import org.finos.symphony.toolkit.workflow.java.Work;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author Gaurav Pancholi
 *
 */



@Work(name = "Reminder List", instructions = "List of Reminders to be conveyed", editable = true)
public class ReminderList {
//	@Autowired
//	static TimedAlerter timedAlerter;

	public Logger LOG =  LoggerFactory.getLogger(ReminderList.class);
	 static List<Reminder> remList = new ArrayList<>();

	public List<Reminder> getRemList() {
		return remList;
	}

	public void setRemList(List<Reminder> remList) {
		this.remList = remList;
	}

	@Exposed(description = "Add an Reminder")
	public static ReminderList addreminder(Reminder cr, History h, Addressable a) {
		Optional<ReminderList> rl =h.getLastFromHistory(ReminderList.class,a);
		//Optional<ReminderList> rl = timedAlerter.handleFeed(a);
		if(rl.isPresent()){
			//rl.get().remList.clear();
			//Optional<ReminderList> fl = timedAlerter.handleFeed(a);
			System.out.println(cr.getDescription());
			rl.get().remList.add(cr);
			return rl.get();
		   }
		    else
		   {
			ReminderList out = new ReminderList();
			out.remList.add(cr);
			return out;
		}
	}

	public static ReminderList deleteReminder(Optional<ReminderList> rl, Reminder reminder){
		if(rl.isPresent()){
//			this.remList.remove(reminder);
//			this.setRemList(rl.get().remList);
			rl.get().getRemList().remove(reminder);

			//LOG.info("Inside remove method of Delete reminder - removed "+reminder);
		}
		return rl.get();
	}

}

