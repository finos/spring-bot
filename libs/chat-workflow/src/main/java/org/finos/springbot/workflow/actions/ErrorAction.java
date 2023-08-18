package org.finos.springbot.workflow.actions;

import org.finos.springbot.workflow.content.Addressable;
import org.finos.springbot.workflow.content.User;

public class ErrorAction implements Action {
	
	private final Addressable a;
	private final Object data;

	public ErrorAction(Addressable a, Object ej) {
		super();
		this.a = a;
		this.data = ej;
	}

	@Override
	public Addressable getAddressable() {
		return a;
	}

	
	@Override
	public Object getData() {
		return data;
	}

	@Override
	public User getUser() {
		return null;
	}

}
