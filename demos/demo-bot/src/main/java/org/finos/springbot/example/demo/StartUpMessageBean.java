package org.finos.springbot.example.demo;

import org.finos.springbot.workflow.content.Addressable;
import org.finos.springbot.workflow.content.Message;
import org.finos.springbot.workflow.conversations.AllConversations;
import org.finos.springbot.workflow.response.MessageResponse;
import org.finos.springbot.workflow.response.handlers.ResponseHandlers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * This shows you how you can send a message to the chat platform when the bot starts up.
 * 
 * @author rob@kite9.com
 *
 */
@Component
public class StartUpMessageBean {

	@Autowired
	ResponseHandlers rh;
	
	@Autowired
	AllConversations convs;
	
	@Value("${room}")
	private String roomName;
	
	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}
	
	@EventListener(ApplicationReadyEvent.class)
	public void doSomethingAfterStartup() {
	    System.out.println("hello world, I have just started up");
	    Addressable a = convs.getExistingChat(roomName);
	    if (a != null) {
	    	rh.accept(new MessageResponse(a, Message.of("Demo Bot Started!")));
	    } else {
	    	System.err.println("Couldn't find a room called "+roomName);
	    }
	}

}
