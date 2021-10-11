package org.finos.springbot.sources.teams.content;

import org.finos.symphony.toolkit.workflow.annotations.Work;
import org.finos.symphony.toolkit.workflow.content.Chat;

@Work(index = false)
public class TeamsChat implements Chat, TeamsAddressable {

	public TeamsChat() {
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
}
