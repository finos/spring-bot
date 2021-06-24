/**
 * 
 */
package org.finos.symphony.toolkit.tools.reminders;

import org.finos.symphony.toolkit.stream.StreamEventConsumer;
import org.finos.symphony.toolkit.stream.welcome.RoomWelcomeEventConsumer;
import org.finos.symphony.toolkit.workflow.Workflow;
import org.finos.symphony.toolkit.workflow.java.workflow.ClassBasedWorkflow;
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
	public Workflow appWorkflow() {
		ClassBasedWorkflow wf = new ClassBasedWorkflow(WorkflowConfig.class.getCanonicalName());
		wf.addClass(Reminder.class);
		wf.addClass(ReminderList.class);
		return wf;
	}

	@Bean
	public StreamEventConsumer consumer() {
		return new StreamEventConsumerImpl();
	}

	@Bean
	RoomWelcomeEventConsumer rwec(MessagesApi ma, UsersApi ua, SymphonyIdentity id) {
		return new RoomWelcomeEventConsumer(ma, ua, id, reminderProperties.getWelcomeMessage());
	}

}