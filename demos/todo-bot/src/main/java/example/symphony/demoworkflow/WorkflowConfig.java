/**
 * 
 */
package example.symphony.demoworkflow;

import org.finos.symphony.toolkit.workflow.Workflow;
import org.finos.symphony.toolkit.workflow.java.workflow.ClassBasedWorkflow;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import example.symphony.demoworkflow.todo.NewItemDetails;
import example.symphony.demoworkflow.todo.ToDoItem;
import example.symphony.demoworkflow.todo.ToDoList;

@Configuration
public class WorkflowConfig {

	@Bean
	public Workflow appWorkflow() {
		ClassBasedWorkflow wf = new ClassBasedWorkflow(WorkflowConfig.class.getCanonicalName());
		wf.addClass(ToDoItem.class);
		wf.addClass(ToDoList.class);
		wf.addClass(NewItemDetails.class);
		return wf;
	}
}