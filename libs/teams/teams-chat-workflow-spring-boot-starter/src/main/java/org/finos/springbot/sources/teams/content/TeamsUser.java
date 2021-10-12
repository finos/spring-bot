package org.finos.springbot.sources.teams.content;

import org.finos.symphony.toolkit.workflow.annotations.Work;
import org.finos.symphony.toolkit.workflow.content.User;

@Work(index = false)
public final class TeamsUser extends TeamsChat implements User {
		
	String email;
	
	public TeamsUser(String id, String name, String email) {
		super(id, name);
		this.email = email;
	}

	@Override
	public String getEmailAddress() {
		return email;
	}

}
