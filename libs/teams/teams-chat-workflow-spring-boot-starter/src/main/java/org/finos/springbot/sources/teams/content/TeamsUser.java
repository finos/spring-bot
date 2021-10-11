package org.finos.springbot.sources.teams.content;

import org.finos.symphony.toolkit.workflow.annotations.Work;
import org.finos.symphony.toolkit.workflow.content.User;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Work(index = false)
public final class TeamsUser implements User, TeamsContent, TeamsAddressable {
		
	String name;
	String email;
	
	public TeamsUser() {
		super();
	}

	@JsonIgnore
	@Override
	public Type getTagType() {
		return USER;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getEmailAddress() {
		return email;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setName(String name) {
		this.name = name;
	}

}
