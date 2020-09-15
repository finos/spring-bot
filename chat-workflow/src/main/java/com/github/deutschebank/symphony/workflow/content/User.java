package com.github.deutschebank.symphony.workflow.content;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonDeserialize(as = UserDef.class)
@JsonSerialize()
public interface User extends Addressable, Tag {
	
	/**
	 * Email address, if known
	 */
	public String getAddress();
	
}
