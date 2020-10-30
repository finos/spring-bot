package org.finos.symphony.toolkit.workflow;

import org.finos.symphony.toolkit.json.EntityJson;
import org.finos.symphony.toolkit.workflow.content.Addressable;
import org.finos.symphony.toolkit.workflow.content.User;

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
