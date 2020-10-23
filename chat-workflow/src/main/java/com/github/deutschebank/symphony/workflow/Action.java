package com.github.deutschebank.symphony.workflow;

import com.github.deutschebank.symphony.json.EntityJson;
import com.github.deutschebank.symphony.workflow.content.Addressable;
import com.github.deutschebank.symphony.workflow.content.User;

public interface Action {

	/**
	 * Where the action happened
	 */
	public Addressable getAddressable();

	/**
	 * Who performed the action.
	 */
	public User getUser();

	/**
	 * Workflow that the action belongs to.
	 */
	public Workflow getWorkflow();
	
	/**
	 * Get the entity data underlying the request.
	 */
	public EntityJson getData();
}
