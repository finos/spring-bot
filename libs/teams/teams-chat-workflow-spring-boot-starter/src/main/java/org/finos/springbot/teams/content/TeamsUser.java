package org.finos.springbot.teams.content;

import java.util.Objects;

import org.finos.springbot.workflow.annotations.Work;
import org.finos.springbot.workflow.content.Tag;
import org.finos.springbot.workflow.content.User;

@Work(index = false)
public final class TeamsUser implements User {
			
	public TeamsUser(String id, String name) {
		this.key = id;
		this.name = name;
	}

	final String key;
	final String name;

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
		if (obj instanceof TeamsChat) {
			return this.key.equals(((TeamsChat) obj).getKey());
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
}
