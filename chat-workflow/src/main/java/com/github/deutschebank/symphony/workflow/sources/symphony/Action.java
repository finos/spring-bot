package com.github.deutschebank.symphony.workflow.sources.symphony;

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
}
