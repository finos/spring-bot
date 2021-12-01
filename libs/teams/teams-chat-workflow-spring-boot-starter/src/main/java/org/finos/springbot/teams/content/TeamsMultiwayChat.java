package org.finos.springbot.teams.content;

import java.util.Objects;

import org.finos.springbot.workflow.annotations.Work;
import org.finos.springbot.workflow.content.Chat;

@Work(index = false)
public class TeamsMultiwayChat implements Chat, TeamsChat {

	private String key;
	private String name;
	
	public TeamsMultiwayChat() {
	}
	
	public TeamsMultiwayChat(String id, String name) {
		this.key = id;
		this.name = name;
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
		if (obj instanceof TeamsMultiwayChat) {
			return this.key.equals(((TeamsMultiwayChat) obj).getKey());
		} else {
			return false;
		}
	}

	public String getKey() {
		return key;
	}
	
}
