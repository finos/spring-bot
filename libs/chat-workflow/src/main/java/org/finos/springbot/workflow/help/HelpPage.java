package org.finos.springbot.workflow.help;

import java.util.List;

import org.finos.springbot.workflow.annotations.Template;
import org.finos.springbot.workflow.annotations.Work;


@Work(index = false)
@Template(view = "help-template")
public class HelpPage {
	
	List<CommandDescription> commands;
	
	public HelpPage() {
		super();
	}

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
