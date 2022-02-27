package org.finos.springbot.example.demo.model;

import org.finos.springbot.workflow.annotations.Template;
import org.finos.springbot.workflow.annotations.Work;
import org.finos.springbot.workflow.help.CommandDescription;
import org.finos.springbot.workflow.help.HelpPage;

import java.util.List;

@Work(index = false)
@Template(view = "custom-help-template")
public class HelpModel extends HelpPage {

    public HelpModel() {
        super();
    }

    public HelpModel(List<CommandDescription> commands) {
        super(commands);
    }
}
