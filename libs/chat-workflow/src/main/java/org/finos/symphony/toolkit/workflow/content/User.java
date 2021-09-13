package org.finos.symphony.toolkit.workflow.content;

public interface User extends Addressable, Tag {
	
	/**
	 * Email address, if known
	 */
	public String getEmailAddress();
	
}
