package org.finos.springbot.example.demo.controller;

import org.finos.springbot.workflow.annotations.ChatRequest;
import org.finos.springbot.workflow.content.Addressable;
import org.finos.springbot.workflow.response.MessageResponse;
import org.springframework.stereotype.Controller;

@Controller
public class DemoController {

    @ChatRequest(value = "test", helpOrder = 1)
    public MessageResponse testBot(Addressable a) {
        return new MessageResponse(a, "Hello from the other side");
    }
}
