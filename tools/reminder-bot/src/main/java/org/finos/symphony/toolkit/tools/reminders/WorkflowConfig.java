/**
 * 
 */
package org.finos.symphony.toolkit.tools.reminders;

import org.finos.springbot.symphony.conversations.SymphonyConversations;
import org.finos.springbot.workflow.actions.consumers.ChatWorkflowErrorHandler;
import org.finos.springbot.workflow.history.History;
import org.finos.springbot.workflow.response.handlers.ResponseHandlers;
import org.finos.symphony.toolkit.stream.welcome.RoomWelcomeEventConsumer;
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
	ReminderProperties reminderProperties;

	@Bean
	RoomWelcomeEventConsumer rwec(MessagesApi ma, UsersApi ua, SymphonyIdentity id) {
		return new RoomWelcomeEventConsumer(ma, ua, id, reminderProperties.getWelcomeMessage());
	}

	@Bean
	TimeFinder timeFinder(ChatWorkflowErrorHandler eh, SymphonyConversations sc, History h, ResponseHandlers rh) {
		return new TimeFinder(eh, sc, h, reminderProperties, rh);
	}
}