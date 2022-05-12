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
    
}

