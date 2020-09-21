/**
 * 
 */
package example.symphony.demo.workflow;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.deutschebank.symphony.workflow.Workflow;
import com.github.deutschebank.symphony.workflow.content.Room;
import com.github.deutschebank.symphony.workflow.content.User;
import com.github.deutschebank.symphony.workflow.java.ClassBasedWorkflow;

import example.symphony.demo.workflow.item.ToDoItem;
import example.symphony.demo.workflow.item.ToDoList;

/**
 * @author rupnsur
 *
 */
@Configuration
public class ToDoListConfig {

	@Bean(name = "workflowClasses")
	public List<Class<?>> workflowClasses() {
		Class<?>[] domainClasses = new Class<?>[] { ToDoItem.class, ToDoList.class };
		return Arrays.asList(domainClasses);
	}

	@Bean(name = "rooms")
	public List<Room> rooms() {
		return Collections.emptyList();
	}

	private ClassBasedWorkflow wf;

	@Bean(name = "administrators")
	public List<User> administrators() {
		return Collections.emptyList();
	}

	@Bean
	public Workflow appWorkflow() {
		wf = new ClassBasedWorkflow("com.db.symphonyp.tabs", administrators(), rooms());
		workflowClasses().stream().forEach(c -> wf.addClass(c));
		return wf;
	}
}
