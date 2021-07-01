package org.finos.symphony.toolkit.workflow.actions;

import org.finos.symphony.toolkit.json.EntityJson;
import org.finos.symphony.toolkit.workflow.Workflow;
import org.finos.symphony.toolkit.workflow.content.Addressable;
import org.finos.symphony.toolkit.workflow.content.User;

/**
 * Contains all the details extracted from the {@link V4SymphonyElementsAction}, and it's originating message.
 * 
 * TODO: Rename to form action or something, as elements is a symphony term.
 * 
 * @author Rob Moffat
 *
 */
public class ElementsAction implements Action {

	private final Workflow workflow;
	private final Object formData;
	private final String action;
	private final EntityJson entityJson;
	private final Addressable a;
	private final User u;
	
	public ElementsAction(Workflow w, Addressable a, User u, Object formData, String action, EntityJson entityJson) {
		super();
		this.workflow = w;
		this.formData = formData;
		this.action = action;
		this.entityJson = entityJson;
		this.a = a;
		this.u = u;
	}

	public Object getFormData() {
		return formData;
	}

	public String getAction() {
		return action;
	}

	@Override
	public EntityJson getData() {
		return entityJson;
	}

	@Override
	public String toString() {
		return "ElementsAction [formData=" + formData + ", action=" + action
				+ ", entityJson=" + entityJson + "]";
	}

	@Override
	public Addressable getAddressable() {
		return a;
	}

	@Override
	public User getUser() {
		return u;
	}

	@Override
	public Workflow getWorkflow() {
		return workflow;
	}
	
	
}
