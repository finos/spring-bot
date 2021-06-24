/**
 * 
 */
package org.finos.symphony.toolkit.tools.reminders;

import com.symphony.api.agent.MessagesApi;
import com.symphony.api.id.SymphonyIdentity;
import com.symphony.api.pod.UsersApi;
import org.finos.symphony.toolkit.stream.StreamEventConsumer;
import org.finos.symphony.toolkit.stream.welcome.RoomWelcomeEventConsumer;
import org.finos.symphony.toolkit.workflow.Workflow;
import org.finos.symphony.toolkit.workflow.java.workflow.ClassBasedWorkflow;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WorkflowConfig {

	public static final String WELCOME_MESSAGE ="<messageML>"
			+ "<p>Welcome to <b>${entity.stream.roomName}</b></p><br />"
			+ "<p>I am the Reminder Bot. If you mention a date or time in your chat message , I will suggest creating a reminder for it.</p><br />"
			+ "<p>type /help for help and to see existing reminders</p>" + "</messageML>";

	@Bean
	public Workflow appWorkflow() {
		ClassBasedWorkflow wf = new ClassBasedWorkflow(WorkflowConfig.class.getCanonicalName());
		wf.addClass(Reminder.class);
		wf.addClass(ReminderList.class);
		return wf;
	}
	@Bean
	public StreamEventConsumer consumer()
	{
     return new StreamEventConsumerImpl();
	}

	@Bean
	RoomWelcomeEventConsumer rwec(MessagesApi ma, UsersApi ua, SymphonyIdentity id) {
		return new RoomWelcomeEventConsumer(ma, ua, id, WELCOME_MESSAGE);
	}


}