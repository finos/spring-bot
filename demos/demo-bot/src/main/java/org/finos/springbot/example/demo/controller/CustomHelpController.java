package org.finos.springbot.example.demo.controller;

import org.finos.springbot.workflow.annotations.ChatRequest;
import org.finos.springbot.workflow.annotations.ChatResponseBody;
import org.finos.springbot.workflow.content.Addressable;
import org.finos.springbot.workflow.content.User;
import org.finos.springbot.workflow.help.HelpController;
import org.finos.springbot.workflow.help.HelpPage;
import org.springframework.stereotype.Controller;

/*
 * Example of overriding framework functionality
 * - Extend framework provided HelpController to override template used for rendering response
 */
@Controller
public class CustomHelpController extends HelpController {

    @ChatRequest(value = "help", description="Display this custom help page")
    @ChatResponseBody(template = "custom-help-template")
    @Override
    public HelpPage handleHelp(Addressable a, User u) {
        /*
         * Using Addressable and User, who invoked the action, you can decide Help menu to show
         * You even have access to list of commands that you can manipulate as per need
         * Finally return a work object with it own template for display
         */
        return super.handleHelp(a, u);
    }
}
