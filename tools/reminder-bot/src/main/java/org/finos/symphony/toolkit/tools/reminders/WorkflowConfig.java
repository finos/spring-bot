/**
 * 
 */
package org.finos.symphony.toolkit.tools.reminders;

import org.finos.symphony.toolkit.spring.api.properties.SymphonyApiProperties;
import org.finos.symphony.toolkit.stream.welcome.RoomWelcomeEventConsumer;
import org.finos.symphony.toolkit.workflow.actions.consumers.ChatWorkflowErrorHandler;
import org.finos.symphony.toolkit.workflow.history.History;
import org.finos.symphony.toolkit.workflow.response.handlers.ResponseHandlers;
import org.finos.symphony.toolkit.workflow.sources.symphony.conversations.SymphonyConversations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.symphony.api.agent.MessagesApi;
import com.symphony.api.id.SymphonyIdentity;
import com.symphony.api.pod.UsersApi;

@EnableConfigurationProperties({ ReminderProperties.class })
@Configuration
public class WorkflowConfig {

	@Autowired
	private ReminderProperties reminderProperties;
	
	@Autowired
	private SymphonyApiProperties apiProperties;

	@Bean
	RoomWelcomeEventConsumer rwec(MessagesApi ma, UsersApi ua, SymphonyIdentity id) {
		return new RoomWelcomeEventConsumer(ma, ua, id, reminderProperties.getWelcomeMessage(), apiProperties);
	}

	@Bean
	TimeFinder timeFinder(ChatWorkflowErrorHandler eh, SymphonyConversations sc, History h, ResponseHandlers rh) {
		return new TimeFinder(eh, sc, h, reminderProperties, rh);
	}
}