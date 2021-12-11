package org.finos.symphony.toolkit.workflow.actions;

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
	 * Get the entity data underlying the request.
	 */
	public Object getData();
		
	public static final Action NULL_ACTION = new Action() {

		@Override
		public Addressable getAddressable() {
			return null;
		}

		@Override
		public User getUser() {
			return null;
		}

		@Override
		public Object getData() {
			return null;
		}
		
	};
	
	/**
	 * Keeps track of the action being handled by the current thread
	 */
	public static final ThreadLocal<Action> CURRENT_ACTION = ThreadLocal.withInitial(() -> NULL_ACTION);
	
}
