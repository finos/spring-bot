package org.finos.symphony.toolkit.tools.reminders;

import org.finos.symphony.toolkit.workflow.annotations.Work;
import org.finos.symphony.toolkit.workflow.sources.symphony.handlers.freemarker.annotations.Dropdown;

@Work
public class TimezonePicker {

	@Dropdown(data = "entity.timezones")
	public String timezone;
	
}
