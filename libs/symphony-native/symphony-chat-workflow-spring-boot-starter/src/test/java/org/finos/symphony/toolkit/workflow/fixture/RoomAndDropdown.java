package org.finos.symphony.toolkit.workflow.fixture;

import java.util.Objects;

import org.finos.symphony.toolkit.workflow.annotations.Work;
import org.finos.symphony.toolkit.workflow.content.Chat;
import org.finos.symphony.toolkit.workflow.sources.symphony.handlers.freemarker.annotations.Dropdown;

@Work
public class RoomAndDropdown {

	Chat chat;
	
	@Dropdown(data = "entity.dynoptions")
	String dynOptions;
	
	public RoomAndDropdown() {
	}

	public RoomAndDropdown(Chat c, String dynOptions) {
		super();
		this.chat = c;
		this.dynOptions = dynOptions;
	}
	
	public Chat getChat() {
		return chat;
	}


	public void setChat(Chat c) {
		this.chat = c;
	}


	public String getDynOptions() {
		return dynOptions;
	}


	public void setDynOptions(String dynOptions) {
		this.dynOptions = dynOptions;
	}


	@Override
	public int hashCode() {
		return Objects.hash(chat, dynOptions);
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RoomAndDropdown other = (RoomAndDropdown) obj;
		return Objects.equals(chat, other.chat) && Objects.equals(dynOptions, other.dynOptions);
	}
	
	
}

