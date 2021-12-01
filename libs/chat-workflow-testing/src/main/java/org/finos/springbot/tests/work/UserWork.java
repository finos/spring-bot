package org.finos.springbot.tests.work;

import org.finos.springbot.workflow.annotations.RequiresUserList;
import org.finos.springbot.workflow.annotations.Work;
import org.finos.springbot.workflow.content.User;

@Work
@RequiresUserList(key = "biglist")
public class UserWork {

	@RequiresUserList(key = "biglist.contents")
	User b;

	@RequiresUserList(key = "smalllist.contents")
	User s;

	public User getB() {
		return b;
	}

	public void setB(User b) {
		this.b = b;
	}

	public User getS() {
		return s;
	}

	public void setS(User s) {
		this.s = s;
	}
	
		
}
