package org.finos.springbot.workflow.actions;

import java.util.Map;

import org.finos.springbot.workflow.content.Addressable;
import org.finos.springbot.workflow.content.User;

/**
 * Created when the user submits a form in the underlying chat platform.
 * 
 * @author Rob Moffat
 *
 */
public class FormAction implements Action {

	private final Object formData;
	private final String action;
	private final Map<String, Object> entityMap;
	private final Addressable a;
	private final User u;
	
	public FormAction(Addressable a, User u, Object formData, String action, Map<String, Object> entityMap) {
		super();
		this.formData = formData;
		this.action = action;
		this.entityMap = entityMap;
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
	public Map<String, Object> getData() {
		return entityMap;
	}

	@Override
	public String toString() {
		return "FormAction [formData=" + formData + ", action=" + action
				+ ", entityMap=" + entityMap + "]";
	}

	@Override
	public Addressable getAddressable() {
		return a;
	}

	@Override
	public User getUser() {
		return u;
	}

	
}
