package org.finos.springbot.teams.content;

import org.finos.springbot.workflow.annotations.Work;
import org.finos.springbot.workflow.content.User;

@Work(index = false)
public final class TeamsUser extends TeamsChat implements User {
			
	public TeamsUser(String id, String name) {
		super(id, name);
	}

}
