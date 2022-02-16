/**
 * 
 */
package org.finos.springbot.tool.reminders;

import org.finos.springbot.tool.reminders.alerter.Scheduler;
import org.finos.springbot.workflow.actions.consumers.ChatWorkflowErrorHandler;
import org.finos.springbot.workflow.content.Message;
import org.finos.springbot.workflow.conversations.AllConversations;
import org.finos.springbot.workflow.history.AllHistory;
import org.finos.springbot.workflow.response.handlers.ResponseHandlers;
import org.finos.springbot.workflow.welcome.RoomWelcomeEventConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@EnableConfigurationProperties({ ReminderProperties.class })
@Configuration
public class WorkflowConfig {

	@Autowired
	private ReminderProperties reminderProperties;
	
	@Bean
	RoomWelcomeEventConsumer rwec(ResponseHandlers rh) {
		return new RoomWelcomeEventConsumer(rh, m -> Message.of(reminderProperties.getWelcomeMessage()));
	}

	@Bean
	TimeFinder timeFinder(ChatWorkflowErrorHandler eh, AllConversations sc, AllHistory h, ResponseHandlers rh) {
		return new TimeFinder(eh, sc, h, reminderProperties, rh);
	}
	
	@Bean
	Scheduler scheduler(ResponseHandlers rh, AllHistory h, AllConversations c) {
		return new Scheduler(rh, h, c);
	}
}