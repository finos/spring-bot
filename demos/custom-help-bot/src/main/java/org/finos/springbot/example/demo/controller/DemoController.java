package org.finos.springbot.example.demo.controller;

import org.finos.springbot.workflow.annotations.ChatRequest;
import org.finos.springbot.workflow.content.Addressable;
import org.finos.springbot.workflow.response.MessageResponse;
import org.springframework.stereotype.Controller;

/**
 * The custom help controller shows the first three options, and the others are on
 * a "more..." expander
 * @author rob@kite9.com
 *
 */
@Controller
public class DemoController {

    @ChatRequest(value = "ping", helpOrder = 1, description = "Help Option 1")
    public MessageResponse pingTest1(Addressable a) {
        return new MessageResponse(a, "Ping!");
    }
    
    @ChatRequest(value = "pong", helpOrder = 1, description = "Help Option 2")
    public MessageResponse pingTest2(Addressable a) {
        return new MessageResponse(a, "Pong!");
    }
    
    @ChatRequest(value = "thing", helpOrder = 1, description = "Help Option 3")
    public MessageResponse pingTest3(Addressable a) {
        return new MessageResponse(a, "You asked for a thing");
    }
    
    @ChatRequest(value = "bling", helpOrder = 1, description = "Help Option 4")
    public MessageResponse pingTest4(Addressable a) {
        return new MessageResponse(a, "You asked for a bling");
    }
    
}

