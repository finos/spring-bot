package org.finos.springbot.example.demo.controller;

import org.finos.springbot.workflow.annotations.ChatRequest;
import org.finos.springbot.workflow.annotations.ChatResponseBody;
import org.finos.springbot.workflow.content.Addressable;
import org.finos.springbot.workflow.content.User;
import org.finos.springbot.workflow.help.CommandDescription;
import org.finos.springbot.workflow.help.HelpController;
import org.finos.springbot.workflow.help.HelpPage;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

/*
 * Example of overriding framework functionality
 * - Extend framework provided HelpController to override template used for rendering response
 */
//@Controller
@Profile(value = "symphony")
public class CustomHelpController extends HelpController {

    private static final String DEFAULT_FORMATTER_PATTERN = "(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])";

    @ChatRequest(value = "help", description="Display this custom help page")
    @ChatResponseBody(template = "custom-help-template")
    @Override
    public HelpPage handleHelp(Addressable a, User u) {
        /*
         * Using Addressable and User, who invoked the action, you can decide Help menu to show
         * You even have access to list of commands that you can manipulate as per need
         * Finally return a work object - HelpPage or its subclass, with it own template for display
         */
        HelpPage helpPage = super.handleHelp(a, u);
        for(CommandDescription command : helpPage.getCommands()) {
            command.setButtonName(formatButtonText(command.getButtonName()));
        }

        return helpPage;
    }

    private String formatButtonText(String buttonName) {
        return Arrays.stream(Optional.ofNullable(buttonName)
                        .orElse("")
                        .split(DEFAULT_FORMATTER_PATTERN))
                        .collect(Collectors.joining(" "));
    }
}
