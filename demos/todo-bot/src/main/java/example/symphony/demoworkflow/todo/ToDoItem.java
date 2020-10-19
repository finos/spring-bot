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
@Work(name = "Item", instructions = "Add/Edit the Item")
public class ToDoItem {

	public enum Status {
		OPEN, COMPLETE
	};
	
	private Integer number;
	private String description;
	private User creator;
	private User assignTo;
	private Status status;

	public ToDoItem() {
		super();
	}

	public ToDoItem(String description, User creator, User assignTo, Status status) {
		super();
		this.description = description;
		this.creator = creator;
		this.assignTo = assignTo;
		this.status = status;
	}

	/**
	 * @return the number
	 */
	public Integer getNumber() {
		return number;
	}

	/**
	 * @param number the number to set
	 */
	public void setNumber(Integer number) {
		this.number = number;
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
	 * @return the creator
	 */
	public User getCreator() {
		return creator;
	}

	/**
	 * @param creator the creator to set
	 */
	public void setCreator(User creator) {
		this.creator = creator;
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

	/**
	 * @return the status
	 */
	public Status getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(Status status) {
		this.status = status;
	}
}
