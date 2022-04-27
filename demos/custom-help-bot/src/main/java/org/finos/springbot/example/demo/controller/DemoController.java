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

    @ChatRequest(value = "ping", helpOrder = 1, description = "Ping test your bot!")
    public MessageResponse pingTest(Addressable a) {
        return new MessageResponse(a, "Hello from the other side");
    }

    @ChatRequest(value = "echo {echoMessage}", helpOrder = 2, description = "Echo back your message")
    public MessageResponse missingChatButton(@ChatVariable(name = "echoMessage") List<Word> msg, Addressable a) {
        String strMsg = msg.stream()
                .map(word -> word.getText())
                .collect(Collectors.joining(" "));

        if(!StringUtils.hasText(strMsg)) {
            strMsg = "Hello";
        }

        return new MessageResponse(a, "Bot echoing - " + strMsg);
    }

    @ChatRequest(value = "botEcho", helpOrder = 2, description = "To demo expandable cards")
    public MessageResponse withChatButton(Addressable a) {
        return new MessageResponse(a, "Echo echo...");
    }
    
}

