/**
 * 
 */
package example.symphony.demoworkflow.todo;

import com.github.deutschebank.symphony.workflow.content.User;
import com.github.deutschebank.symphony.workflow.java.Work;

/**
 * @author rupnsur
 *
 */
@Work(name = "New Item", instructions = "Add the new item")
public class NewItemDetails {
	
	String description;
	User assignTo;
	
	public NewItemDetails() {
		super();
	}
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	/**
	 * @return the assignTo
	 */
	public User getAssignTo() {
		return assignTo;
	}
	/**
	 * @param assignTo the assignTo to set
	 */
	public void setAssignTo(User assignTo) {
		this.assignTo = assignTo;
	}
	
}
