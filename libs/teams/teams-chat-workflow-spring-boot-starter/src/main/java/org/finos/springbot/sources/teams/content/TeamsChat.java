package org.finos.springbot.sources.teams.content;

import java.util.Objects;

import org.finos.symphony.toolkit.workflow.annotations.Work;
import org.finos.symphony.toolkit.workflow.content.Chat;
import org.finos.symphony.toolkit.workflow.content.Tag;

@Work(index = false)
public class TeamsChat implements Chat, TeamsAddressable, TeamsContent, Tag {

	final String id;
	final String name;
	
	public TeamsChat(String id, String name) {
		this.id = id;
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
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof TeamsChat) {
			return this.id.equals(((TeamsChat) obj).getId());
		} else {
			return false;
		}
	}

	@Override
	public Type getTagType() {
		return Tag.MENTION;
	}

	public String getId() {
		return id;
	}
	
}
