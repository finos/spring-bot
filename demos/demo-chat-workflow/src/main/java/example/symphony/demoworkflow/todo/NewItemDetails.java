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
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((assignTo == null) ? 0 : assignTo.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		NewItemDetails other = (NewItemDetails) obj;
		if (assignTo == null) {
			if (other.assignTo != null)
				return false;
		} else if (!assignTo.equals(other.assignTo))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		return true;
	}
	
	
}
