package com.symphony.integration;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

public class User {

	public String emailAddress;
	public String username;
	public String displayName;
	@JsonInclude(value=Include.NON_NULL)
	public String id;
}
