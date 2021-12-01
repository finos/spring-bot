package org.finos.springbot.teams.content;

import java.util.Objects;

import org.finos.springbot.workflow.annotations.Work;
import org.finos.springbot.workflow.content.Chat;
import org.finos.springbot.workflow.content.Tag;

/**
 * A channel exists within a team.  You can @-mention it.  It contains conversations.
 * 
 * @author rob@kite9.com
 *
 */
@Work(index = false)
public class TeamsChannel implements Chat, TeamsChat, TeamsMention {

	String key;
	String name;
	
	public void setKey(String key) {
		this.key = key;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public TeamsChannel() {
		super();
	}

	public TeamsChannel(String id, String name) {
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
