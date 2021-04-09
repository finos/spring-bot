/**
 * 
 */
package org.finos.symphony.webhookbot;

import org.finos.symphony.toolkit.stream.welcome.RoomWelcomeEventConsumer;
import org.finos.symphony.toolkit.workflow.Workflow;
import org.finos.symphony.toolkit.workflow.java.workflow.ClassBasedWorkflow;
import org.finos.symphony.webhookbot.domain.WebHook;
import org.finos.symphony.webhookbot.domain.WebHookOps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.symphony.api.agent.MessagesApi;
import com.symphony.api.id.SymphonyIdentity;
import com.symphony.api.pod.UsersApi;

@Configuration
@EnableConfigurationProperties(WebhookProperties.class)
public class WebhookConfig  {
	
	@Autowired
	WebhookProperties properties;
	
	@Value("${webhook.article:https://zapier.com/blog/what-are-webhooks}")
	private String articleUrl;
	
	@Value("${webhook.baseUrl:http://localhost:${server.port}}")
	private String baseUrl;
	
	public String getWelcomeMessage() {
		return  "<messageML>"
			+ "<p>Hi, welcome to <b>${entity.stream.roomName}</b></p><br />"
			+ "<p>This room supports <a href=\""+articleUrl+"\" >webhooks</a>. "
			+ "To configure a new webhook feed in this room type:</p><br /> "
			+ "<code>/newhook #subjecthashtag Display Name</code></messageML>";
	}

	@Bean
	public Workflow appWorkflow() {
		ClassBasedWorkflow wf = new ClassBasedWorkflow(WebhookConfig.class.getCanonicalName());
		wf.addClass(WebHookOps.class);
		wf.addClass(WebHook.class);
		return wf;
	}
	
	@Bean
	public Helpers helpers() {
		return new Helpers(baseUrl);
	}
	
	@Bean
	RoomWelcomeEventConsumer rwec(MessagesApi ma, UsersApi ua, SymphonyIdentity id) {
		return new RoomWelcomeEventConsumer(ma, ua, id, getWelcomeMessage());
	}
	
}