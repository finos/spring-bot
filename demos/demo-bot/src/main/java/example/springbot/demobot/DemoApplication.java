package example.springbot.demobot;

import org.finos.springbot.workflow.actions.SimpleMessageAction;
import org.finos.springbot.workflow.actions.consumers.ActionConsumer;
import org.finos.springbot.workflow.content.Addressable;
import org.finos.springbot.workflow.content.Message;
import org.finos.springbot.workflow.conversations.AllConversations;
import org.finos.springbot.workflow.response.MessageResponse;
import org.finos.springbot.workflow.response.handlers.ResponseHandlers;
import org.finos.springbot.workflow.welcome.RoomWelcomeEventConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

@SpringBootApplication
@Configuration
public class DemoApplication {
	
	@Autowired
	private ResponseHandlers rh;
	
	@Autowired
	AllConversations convs;
	
	// NB: Bot must belong to the named room
	@Value("${room}")
	private String roomName;
	
	
	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@EventListener(ApplicationReadyEvent.class)
	public void doSomethingAfterStartup() {
	    System.out.println("hello world, I have just started up");
	    Addressable a = convs.getExistingChat(roomName);
	    rh.accept(new MessageResponse(a, Message.of("Demo Bot Started!")));
	}
	 
	@Bean
	public ActionConsumer consumer() {
		return event -> {
			if (event instanceof SimpleMessageAction) {
				// reply with original content
				SimpleMessageAction in = (SimpleMessageAction) event;
				// reply to the room the message came from
				Addressable from = in.getAddressable();
				Message msg = in.getMessage();
				MessageResponse mr = new MessageResponse(from, msg);
				rh.accept(mr);
				
			}
		};
	}
	
	@Bean
	public RoomWelcomeEventConsumer welcomeMessages() {
		return new RoomWelcomeEventConsumer(rh);
	}

}
