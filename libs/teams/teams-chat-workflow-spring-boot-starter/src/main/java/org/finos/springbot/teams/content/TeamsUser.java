package org.finos.springbot.teams.content;

import java.util.Objects;

import org.finos.springbot.workflow.annotations.Work;
import org.finos.springbot.workflow.content.Tag;
import org.finos.springbot.workflow.content.User;

@Work(index = false)
public final class TeamsUser implements User, TeamsMention, TeamsAddressable {
	
	public TeamsUser() {
	}
			
	public TeamsUser(String id, String name, String aadObjectId) {
		this.key = id;
		this.name = name;
		this.aadObjectId = aadObjectId;
	}
	
	private String aadObjectId;
	private String key;
	private String name;

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getText() {
		return "@"+name;
	}

	@Override
	public int hashCode() {
		return Objects.hash(key);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof TeamsMention) {
			return this.key.equals(((TeamsMention) obj).getKey());
		} else {
			return false;
		}
	}

	@Override
	public Type getTagType() {
		return Tag.MENTION;
	}

	public String getKey() {
		return key;
	}
	
	public String getAadObjectId() {
		return aadObjectId;
	}


}
