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
