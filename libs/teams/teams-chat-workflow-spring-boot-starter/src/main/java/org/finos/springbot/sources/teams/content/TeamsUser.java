package org.finos.springbot.sources.teams.content;

import org.finos.symphony.toolkit.workflow.annotations.Work;
import org.finos.symphony.toolkit.workflow.content.User;

@Work(index = false)
public final class TeamsUser extends TeamsChat implements User {
			
	public TeamsUser(String id, String name) {
		super(id, name);
	}

}
