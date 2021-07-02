package org.finos.symphony.toolkit.workflow.help;

import java.util.List;

import org.finos.symphony.toolkit.workflow.actions.Template;
import org.finos.symphony.toolkit.workflow.annotations.Work;


@Work
@Template(view = "help-page")
public class HelpPage {
	
	List<CommandDescription> commands;

	public HelpPage(List<CommandDescription> commands2) {
		this.commands = commands2;
	}

	public List<CommandDescription> getCommands() {
		return commands;
	}

	public void setCommands(List<CommandDescription> commands) {
		this.commands = commands;
	}

}
