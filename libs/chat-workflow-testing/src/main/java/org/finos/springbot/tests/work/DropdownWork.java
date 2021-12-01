package org.finos.springbot.tests.work;

import org.finos.springbot.workflow.annotations.Dropdown;

public class DropdownWork {
	
	@Dropdown(data = "options.contents", key = "key", name="name")
	String s;
	
	
	public String getS() {
		return s;
	}

	public void setS(String s) {
		this.s = s;
	}
	
	
	
}
