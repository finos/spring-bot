package org.finos.symphony.toolkit.workflow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.finos.springbot.sources.teams.content.TeamsChat;
import org.finos.springbot.sources.teams.handlers.FormMessageMLConverter;
import org.finos.springbot.sources.teams.json.EntityJsonConverter;
import org.finos.springbot.sources.teams.json.RoomList;
import org.finos.symphony.toolkit.workflow.annotations.WorkMode;
import org.finos.symphony.toolkit.workflow.fixture.RoomAndDropdown;
import org.finos.symphony.toolkit.workflow.form.Button;
import org.finos.symphony.toolkit.workflow.form.Button.Type;
import org.finos.symphony.toolkit.workflow.form.ButtonList;
import org.finos.symphony.toolkit.workflow.response.WorkResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Validator;

public class RoomAndDropdownMessageML extends AbstractMockSymphonyTest {

	@Autowired
	FormMessageMLConverter messageMlConverter;
	
	@Autowired
	Validator validator;

	@Autowired
	EntityJsonConverter ejc;
	
	protected RoomList getSomeRooms() {
		TeamsChat a = new TeamsChat("room one", "one");
		TeamsChat b = new TeamsChat("tesxt room", "abc123");
		TeamsChat c = new TeamsChat("room three", "three");
		RoomList out = new RoomList();
		out.add(a);
		out.add(b);
		out.add(c);
		return out;
	}
	
	
	protected Map<String, String> getDynamicOptions() {
		Map<String, String> out = new HashMap<String, String>();
		out.put("a+", "Amazing");
		out.put("a", "Great");
		out.put("b", "Good");
		out.put("c", "So-So");
		out.put("d", "Awful");
		out.put("a-", "Abysmal");
		return out;
	}

	protected WorkResponse dropdownsWork(WorkMode wm) {
		TeamsChat theRoom = new TeamsChat("tesxt room", "abc123");
		String strangeOption = "a";

		RoomAndDropdown to4 = new RoomAndDropdown(theRoom, strangeOption);
		
		Button submit = new Button("submit", Type.ACTION, "GO");
		WorkResponse wr = new WorkResponse(theRoom, to4, wm);
		
		// ensure buttons
		ButtonList bl = (ButtonList) wr.getData().get(ButtonList.KEY);
		bl.add(submit);
		
		// ensure rooms
		wr.getData().put("rooms", getSomeRooms());
		
		// ensure dynamic dropdown options
		wr.getData().put("dynoptions", getDynamicOptions());
		
		return wr;
	}

	@Test
	public void testDropdownView() throws Exception {
		WorkResponse wr = dropdownsWork(WorkMode.VIEW);
		testTemplating(wr, "abc123", "testDropdownView.ml", "testDropdownView.json");
	}
	
	@Test
	public void testDropdownEdit() throws Exception {
		WorkResponse wr = dropdownsWork(WorkMode.EDIT);
		testTemplating(wr, "abc123", "testDropdownEdit.ml", "testDropdownEdit.json");
	}

	
	

}
