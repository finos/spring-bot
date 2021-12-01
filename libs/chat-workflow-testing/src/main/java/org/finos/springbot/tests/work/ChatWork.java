package org.finos.springbot.tests.work;

import org.finos.springbot.workflow.annotations.Work;
import org.finos.springbot.workflow.content.Chat;

@Work
public class ChatWork {

	Chat s;

	public Chat getS() {
		return s;
	}

	public void setS(Chat s) {
		this.s = s;
	}
	
}
