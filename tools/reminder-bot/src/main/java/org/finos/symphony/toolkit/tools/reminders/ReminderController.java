package org.finos.symphony.toolkit.tools.reminders;

import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.finos.symphony.toolkit.workflow.annotations.ChatButton;
import org.finos.symphony.toolkit.workflow.annotations.ChatRequest;
import org.finos.symphony.toolkit.workflow.annotations.ChatResponseBody;
import org.finos.symphony.toolkit.workflow.annotations.ChatVariable;
import org.finos.symphony.toolkit.workflow.annotations.WorkMode;
import org.finos.symphony.toolkit.workflow.content.Addressable;
import org.finos.symphony.toolkit.workflow.content.Content;
import org.finos.symphony.toolkit.workflow.content.Paragraph;
import org.finos.symphony.toolkit.workflow.content.Table;
import org.finos.symphony.toolkit.workflow.content.UnorderedList;
import org.finos.symphony.toolkit.workflow.content.User;
import org.finos.symphony.toolkit.workflow.content.Word;
import org.finos.symphony.toolkit.workflow.history.History;
import org.finos.symphony.toolkit.workflow.response.MessageResponse;
import org.finos.symphony.toolkit.workflow.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class ReminderController {
	
	@Autowired
	History h;
	
	@Autowired
	ReminderProperties rp;

	@ChatButton(buttonText = "Add", value = Reminder.class, showWhen = WorkMode.EDIT)
	public ReminderList addreminder(Reminder cr, Addressable a, User author) {
		ReminderList rl = list(a);
		rl.reminders.add(cr);
		return rl;
	
	}

	@ChatRequest(value = "list", description  = "Show list of Reminders")
	@ChatResponseBody(workMode = WorkMode.VIEW)
	public ReminderList list(Addressable a) {
		Optional<ReminderList> rl = h.getLastFromHistory(ReminderList.class, a);

		if (rl.isPresent()) {
			return rl.get();
		} else {
			ReminderList out = new ReminderList();
			out.setTimeZone(rp.getDefaultTimeZone());
			out.setRemindBefore(rp.getDefaultRemindBefore());
			return out;
		}
	}

	@ChatButton(buttonText = "save", value = ReminderList.class, showWhen = WorkMode.EDIT)
	@ChatResponseBody(workMode = WorkMode.VIEW)
	public ReminderList save(ReminderList rl) {
		return rl;
	}
	
	@ChatButton(buttonText = "edit", value = ReminderList.class, showWhen = WorkMode.VIEW)
	@ChatResponseBody(workMode = WorkMode.EDIT)
	public ReminderList edit(ReminderList rl) {
		return rl;
	}
		
	public Map<String, String> timezones() {
		Map<String, String> zoneMap = ZoneId.getAvailableZoneIds().stream()
			.sorted()
			.collect(Collectors.toMap(k -> k, k -> k));
		return zoneMap;	
	}
	
	
	@ChatRequest(value="timezones", description = "List Time Zones") 
	public Response timezones(Addressable a) {
		Map<String, List<String>> zoneMap = ZoneId.getAvailableZoneIds().stream()
			.sorted()
			.collect(Collectors.groupingBy(k -> {
				int slashIndex = k.indexOf("/");
				if (slashIndex == -1) {
					return "none";
				} else {
					return k.substring(0, slashIndex);
				}
			}));
		
		List<? extends Content> headers = Word.build("Region Zone");
		
		List<List<? extends Content>> tableCells = zoneMap.keySet().stream()
				.map(m -> Arrays.asList(Word.of(m), bulletsOf(zoneMap.get(m))))
				.collect(Collectors.toList());
		
		Table t = Table.of(headers, tableCells);
		return new MessageResponse(a, t);
		
	
	}
	
	private Content bulletsOf(List<String> list) {
		return UnorderedList.of(
			list.stream()
				.map(w -> Paragraph.of(Arrays.asList(Word.of(w))))
				.collect(Collectors.toList()));
	}

	@ChatRequest(description = "Set Time Zone. e.g \"zone Europe/London\"", value="zone {zonename}") 
	public ReminderList zone(Addressable a, @ChatVariable("zonename") Word zoneName) {
		ReminderList rl = list(a);
		ZoneId newZone = ZoneId.of(zoneName.getText());
		rl.setTimeZone(newZone);
		return rl;	
	}

	@ChatRequest(description = "Set Remind Before Duration . e.g. remindbefore 30", value="remindbefore {duration}")
	public ReminderList remindbefore(Addressable a, @ChatVariable(required = true, name = "duration") Word duration){
		ReminderList rl = list(a);
		int remindBefore = Integer.parseInt(duration.getText());
		rl.setRemindBefore(remindBefore);
		return rl;
	}
  
}
