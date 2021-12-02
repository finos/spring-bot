package org.finos.springbot.teams.content;

import java.util.Objects;

import org.finos.springbot.workflow.annotations.Work;
import org.finos.springbot.workflow.content.Chat;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * A Conversation happens inside a channel.
 * 
 * @author rob@kite9.com
 *
 */
@Work(index = false)
public class TeamsConversation implements Chat, TeamsAddressable {

	String key;
	String name;

	public TeamsConversation() {
		super();
	}

	public TeamsConversation(String id, String name) {
		this.key = id;
		this.name = name;
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
	public int hashCode() {
		return Objects.hash(key);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof TeamsConversation) {
			return this.key.equals(((TeamsConversation) obj).getKey());
		} else {
			return false;
		}
	}

	public String getKey() {
		return key;
	}
	

	public void setKey(String key) {
		this.key = key;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}
