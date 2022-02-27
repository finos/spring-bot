package org.finos.springbot.example.demo.controller;

import org.finos.springbot.example.demo.model.HelpModel;
import org.finos.springbot.workflow.annotations.ChatRequest;
import org.finos.springbot.workflow.annotations.Help;
import org.finos.springbot.workflow.content.Addressable;
import org.finos.springbot.workflow.content.User;
import org.finos.springbot.workflow.help.CommandDescription;
import org.finos.springbot.workflow.response.MessageResponse;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class DemoController {

    @ChatRequest(value = "test", helpOrder = 1)
    public MessageResponse testBot(Addressable a) {
        return new MessageResponse(a, "Hello from the other side");
    }

    @Help
    public HelpModel botHelp(Addressable a, User author, List<CommandDescription> commands) {
        /*
         * Using Addressable and User, who invoked the action, you can decide Help menu to show
         * You even have list of commands that you can manipulate as per need
         * Finally return a work object with it own template for display
         */
        return new HelpModel(commands);
    }
}
