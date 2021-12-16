package example.symphony.demobot;

import org.finos.springbot.symphony.stream.StreamEventConsumer;
import org.finos.springbot.symphony.stream.welcome.RoomWelcomeEventConsumer;
import org.finos.springbot.workflow.data.EntityJsonConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

import com.symphony.api.agent.MessagesApi;
import com.symphony.api.id.SymphonyIdentity;
import com.symphony.api.model.V4MessageSent;
import com.symphony.api.pod.UsersApi;

@SpringBootApplication
@Configuration
public class DemoApplication {
		
	@Autowired
	private MessagesApi messagesApi;
	
	@Autowired
	private UsersApi usersApi;
	
	@Autowired
	private SymphonyIdentity id;
	
	@Autowired
	private EntityJsonConverter ejc;
	
	@Value("${room}")
	private String streamId;

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@EventListener(ApplicationReadyEvent.class)
	public void doSomethingAfterStartup() {
	    System.out.println("hello world, I have just started up");
	    messagesApi.v4StreamSidMessageCreatePost(null, streamId, "<messageML>sometestmessage</messageML>", null, null, null, null, null);
	}
	 
	@Bean
	public StreamEventConsumer consumer() {
		return event -> {
			V4MessageSent ms = event.getPayload().getMessageSent();
			if ((ms != null) && (!ms.getMessage().getUser().getEmail().equals(id.getEmail()))) {
				
				// echo the message back
				messagesApi.v4StreamSidMessageCreatePost(null, 
					ms.getMessage().getStream().getStreamId(), 		// reply to the room the message came from
					ms.getMessage().getMessage(), 					// reply with original content
				null, null, null, null, null);	
				
			}
			
		};
	}
	
	@Bean
	public StreamEventConsumer welcomeMessages() {
		return new RoomWelcomeEventConsumer(messagesApi, usersApi, id, ejc);
	}

}
