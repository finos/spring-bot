package org.finos.symphony.toolkit.tools.reminders;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.finos.symphony.toolkit.json.EntityJson;
import org.finos.symphony.toolkit.workflow.Workflow;
import org.finos.symphony.toolkit.workflow.content.Addressable;
import org.finos.symphony.toolkit.workflow.content.Author;
import org.finos.symphony.toolkit.workflow.content.Word;
import org.finos.symphony.toolkit.workflow.history.History;
import org.finos.symphony.toolkit.workflow.java.Exposed;
import org.finos.symphony.toolkit.workflow.java.Work;
import org.finos.symphony.toolkit.workflow.response.MessageResponse;
import org.finos.symphony.toolkit.workflow.response.Response;
import org.finos.symphony.toolkit.workflow.sources.symphony.Template;

/**
 * @author Gaurav Pancholi
 *
 */

@Work(name = "Reminder List", instructions = "List of reminders to be conveyed", editable = true)
@Template(edit = "classpath:/edit-reminder-list.ftl")
public class ReminderList {

	public ReminderList() {
		super();
	}

	public ReminderList(ReminderList old) {
		super();
		reminders = new ArrayList<>(old.getReminders());
		timeZone = old.timeZone;
	}

	
	List<Reminder> reminders = new ArrayList<>();

	ZoneId timeZone;

	public ZoneId getTimeZone() {
		return timeZone;
	}

	public void setTimeZone(ZoneId timeZone) {
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
	
	@Exposed(description = "List Time Zones", isMessage=true) 
	public static Response listTimeZones(Workflow wf, Addressable a) {
		Map<String, List<String>> zoneMap = ZoneId.getAvailableZoneIds().stream()
			.sorted()
			.collect(Collectors.groupingBy(k -> {
				int slashIndex = k.indexOf("/");
				if (slashIndex == -1) {
					return "<i>none</i>";
				} else {
					return k.substring(0, slashIndex);
				}
			}));
		
		String zoneList = zoneMap.keySet().stream()
				.map(m -> {
					StringBuilder out = new StringBuilder();
					out.append("<tr><td>"+ m + "</td><td><ul>");
					out.append(
						zoneMap.get(m).stream()
							.map(tz -> "<li>"+tz+"</li>")
							.reduce(String::concat)
							.orElse(""));
					out.append("</ul></td></tr>");
					return out.toString();
				})
				.reduce(String::concat)
				.orElse("");


		
		return new MessageResponse(wf, a, new EntityJson(), "List of Time Zones", 
				"Type \"setZone &lt;name&gt;\" from the options below. e.g \"setZone Europe/London\"", 
				"<table><tr><th>Region</th><th>Zone</th></tr>"+zoneList+"</table>");
		
	
	}
	
	@Exposed(description = "Set Time Zone. e.g \"setZone Europe/London\"", isMessage=true) 
	public static ReminderList setZone(Workflow wf, History h, ReminderProperties rp, Addressable a, Word setZone, Word zoneName) {
		ReminderList rl = listReminders(h, a, rp);
		ZoneId newZone = ZoneId.of(zoneName.getText());
		rl.setTimeZone(newZone);
		return rl;	
	}
}
