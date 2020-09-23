/**
 * 
 */
package example.symphony.demoworkflow;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.deutschebank.symphony.workflow.Workflow;
import com.github.deutschebank.symphony.workflow.java.ClassBasedWorkflow;

import example.symphony.demoworkflow.expenses.Claim;
import example.symphony.demoworkflow.todo.ToDoItem;
import example.symphony.demoworkflow.todo.ToDoList;

/**
 * @author rupnsur
 *
 */
@Configuration
public class WorkflowConfig {

	@Bean
	public Workflow appWorkflow() {
		ClassBasedWorkflow wf = new ClassBasedWorkflow(WorkflowConfig.class.getCanonicalName());
		wf.addClass(Claim.class);
		wf.addClass(ToDoItem.class);
		wf.addClass(ToDoList.class);
		return wf;
	}
}
