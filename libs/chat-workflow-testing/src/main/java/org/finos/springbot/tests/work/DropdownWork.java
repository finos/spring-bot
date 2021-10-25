package org.finos.springbot.tests.work;

import java.util.HashMap;
import java.util.Map;

import org.finos.springbot.workflow.annotations.Dropdown;

public class DropdownWork {

	@SuppressWarnings("serial")
	public static Map<String, String> options = new HashMap<String, String>() {{
		put("A", "A Man");
		put("B", "A Plan");
		put("C", "A Canal");
		put("D", "Panama");
	}};
	
	
	@Dropdown(data = "options", key = "key", value="value")
	String s;
	
	
	public String getS() {
		return s;
	}

	public void setS(String s) {
		this.s = s;
	}
	
	
	
}
