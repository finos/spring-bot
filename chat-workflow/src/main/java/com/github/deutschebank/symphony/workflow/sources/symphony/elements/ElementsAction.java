package com.github.deutschebank.symphony.workflow.sources.symphony.elements;

import com.github.deutschebank.symphony.workflow.Workflow;
import com.github.deutschebank.symphony.workflow.content.Room;
import com.github.deutschebank.symphony.workflow.content.User;
import com.github.deutschebank.symphony.workflow.sources.symphony.Action;
import com.symphony.api.model.V4SymphonyElementsAction;

/**
 * Contains all the details extracted from the {@link V4SymphonyElementsAction}, and it's originating message.
 * 
 * @author Rob Moffat
 *
 */
public class ElementsAction implements Action {

	private final Workflow workflow;
	private final Object formData;
	private final String action;
	private final Object workflowObject;
	private final Room r;
	private final User u;
	
	public ElementsAction(Workflow w, Room r, User u, Object formData, String action, Object workflowObject) {
		super();
		this.workflow = w;
		this.formData = formData;
		this.action = action;
		this.workflowObject = workflowObject;
		this.r = r;
		this.u = u;
	}

	public Object getFormData() {
		return formData;
	}

	public String getAction() {
		return action;
	}

	public Object getWorkflowObject() {
		return workflowObject;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((action == null) ? 0 : action.hashCode());
		result = prime * result + ((formData == null) ? 0 : formData.hashCode());
		result = prime * result + ((workflowObject == null) ? 0 : workflowObject.hashCode());
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
		ElementsAction other = (ElementsAction) obj;
		if (action == null) {
			if (other.action != null)
				return false;
		} else if (!action.equals(other.action))
			return false;
		if (formData == null) {
			if (other.formData != null)
				return false;
		} else if (!formData.equals(other.formData))
			return false;
		if (workflowObject == null) {
			if (other.workflowObject != null)
				return false;
		} else if (!workflowObject.equals(other.workflowObject))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ElementsAction [formData=" + formData + ", action=" + action
				+ ", workflowObject=" + workflowObject + "]";
	}

	@Override
	public Room getRoom() {
		return r;
	}

	@Override
	public User getUser() {
		return u;
	}

	public Workflow getWorkflow() {
		return workflow;
	}
	
	
}
