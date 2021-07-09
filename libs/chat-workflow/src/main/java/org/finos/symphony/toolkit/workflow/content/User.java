package org.finos.symphony.toolkit.workflow.content;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize()
public interface User extends Addressable, Tag {
	
	/**
	 * Email address, if known
	 */
	public String getEmailAddress();
	
}
