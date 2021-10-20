package org.finos.springbot.workflow.templating.fixture;

import org.finos.springbot.workflow.annotations.Work;
import org.finos.springbot.workflow.content.User;

@Work
public class UserWork {

	User s;

	public User getS() {
		return s;
	}

	public void setS(User s) {
		this.s = s;
	}
	
		
}
