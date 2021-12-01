package org.finos.springbot.teams.content;

import java.util.Objects;

import org.finos.springbot.workflow.annotations.Work;
import org.finos.springbot.workflow.content.Chat;
import org.finos.springbot.workflow.content.Tag;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Work(index = false)
public class TeamsConversation implements Chat, TeamsAddressable, TeamsMention {

	final String aadGroupId;
	final String key;
	final String name;
	
	public TeamsConversation(String aadGroupId, String id, String name) {
		this.aadGroupId = aadGroupId;
		this.key = id;
		this.name = name;
	}

	public String getAadGroupId() {
		return aadGroupId;
	}
	
	@JsonIgnore
	public String getMessageId() {
		return key.substring(key.lastIndexOf("messageid=")+10);
	}
	
	@JsonIgnore
	public String getChannelId() {
		return key.substring(0, key.lastIndexOf(";"));
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
