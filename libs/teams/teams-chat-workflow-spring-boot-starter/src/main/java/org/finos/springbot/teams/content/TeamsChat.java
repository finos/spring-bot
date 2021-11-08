package org.finos.springbot.teams.content;

import java.util.Objects;

import org.finos.springbot.workflow.annotations.Work;
import org.finos.springbot.workflow.content.Chat;
import org.finos.springbot.workflow.content.Tag;

@Work(index = false)
public class TeamsChat implements Chat, TeamsAddressable, TeamsMention {

	final String key;
	final String name;
	
	public TeamsChat(String id, String name) {
		this.key = id;
		this.name = name;
	}

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
	
}
