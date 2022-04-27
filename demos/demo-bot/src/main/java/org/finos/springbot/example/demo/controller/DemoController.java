package org.finos.springbot.example.demo.controller;

import org.finos.springbot.workflow.annotations.ChatRequest;
import org.finos.springbot.workflow.annotations.ChatVariable;
import org.finos.springbot.workflow.content.Addressable;
import org.finos.springbot.workflow.content.Word;
import org.finos.springbot.workflow.response.MessageResponse;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class DemoController {

    @ChatRequest(value = "test", helpOrder = 1, description = "Ping test your bot!")
    public MessageResponse testBot(Addressable a) {
        return new MessageResponse(a, "Hello from the other side");
    }

    /*
     * Framework recognizes that you forgot to mention "isButtonOnHelpPage = false" in @ChatRequest mapping for below case
     * Since you are expecting parameter as part of command input, it turns off display of button for this mapping in help menu
     */
    @ChatRequest(value = "echo {echoMessage}", helpOrder = 2, description = "Button display suppressed as this command requires parameter")
    public MessageResponse missingChatButton(@ChatVariable(name = "echoMessage") List<Word> msg, Addressable a) {
        String strMsg = msg.stream()
                .map(word -> word.getText())
                .collect(Collectors.joining(" "));

        if(!StringUtils.hasText(strMsg)) {
            strMsg = "Hello";
        }

        return new MessageResponse(a, "Bot echoing - " + strMsg);
    }

    @ChatRequest(value = "botEcho", helpOrder = 2, description = "To test help order")
    public MessageResponse withChatButton(Addressable a) {
        return new MessageResponse(a, "Echo echo...");
    }
    
 
}

