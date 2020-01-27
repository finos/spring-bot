package example.symphony.demobot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import com.symphony.api.agent.DatafeedApi;
import com.symphony.api.agent.MessagesApi;
import com.symphony.api.bindings.Streams;
import com.symphony.api.id.SymphonyIdentity;
import com.symphony.api.model.Datafeed;

@SpringBootApplication
public class DemoApplication {
	
	private static final Logger LOG = LoggerFactory.getLogger(DemoApplication.class);
	
	@Autowired
	MessagesApi messagesApi;
	
	@Autowired
	DatafeedApi datafeedApi; 
	
	@Autowired
	SymphonyIdentity id;
	
	@Value("${room:StQv5mK1u-06afIwrhtN1n___pPiNy8tdA==}")
	String streamId;

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@EventListener(ApplicationReadyEvent.class)
	public void doSomethingAfterStartup() {
	    System.out.println("hello world, I have just started up");
	    messagesApi.v4StreamSidMessageCreatePost(null, streamId, "<messageML>sometestmessage</messageML>", null, null, null, null, null);
	    
	    // create a datafeed
	    Datafeed df = datafeedApi.v4DatafeedCreatePost(null, null);
	    
	    Streams.createWorker(() -> datafeedApi.v4DatafeedIdReadGet(df.getId(), null, null, 50), e -> LOG.error("Problem with Symphony!", e))
	    	.stream()
	    	.filter(e -> e.getType().equals("MESSAGESENT"))
	    	.map(e -> e.getPayload().getMessageSent().getMessage())
	    	.filter(m -> !m.getUser().getEmail().equals(id.getEmail()))
	    	.forEach(m -> messagesApi.v4StreamSidMessageCreatePost(null, streamId, m.getMessage(), null, null, null, null, null));

	    	
	}
}
