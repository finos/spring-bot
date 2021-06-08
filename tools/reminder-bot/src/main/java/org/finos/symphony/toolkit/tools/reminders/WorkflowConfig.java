/**
 * 
 */
package org.finos.symphony.toolkit.tools.reminders;

import org.finos.symphony.toolkit.stream.StreamEventConsumer;
import org.finos.symphony.toolkit.workflow.Workflow;
import org.finos.symphony.toolkit.workflow.java.workflow.ClassBasedWorkflow;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WorkflowConfig {

	@Bean
	public Workflow appWorkflow() {
		ClassBasedWorkflow wf = new ClassBasedWorkflow(WorkflowConfig.class.getCanonicalName());
		wf.addClass(Reminder.class);
		wf.addClass(ReminderList.class);
		//wf.addClass(Reminder.class);
		return wf;
	}
	@Bean
	public StreamEventConsumer consumer()
	{
     return new StreamEventConsumerImpl();
	}


}