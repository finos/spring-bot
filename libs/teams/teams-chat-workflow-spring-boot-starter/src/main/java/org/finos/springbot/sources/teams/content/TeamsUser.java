package org.finos.springbot.sources.teams.content;

import org.finos.symphony.toolkit.workflow.annotations.Work;
import org.finos.symphony.toolkit.workflow.content.User;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Work(index = false)
public final class TeamsUser implements User, TeamsContent, TeamsAddressable {
		
	public TeamsUser() {
		super();
	}

	@JsonIgnore
	@Override
	public Type getTagType() {
		return USER;
	}

	

}
