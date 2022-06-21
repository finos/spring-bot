package org.finos.springbot.symphony.form;

import java.util.Objects;

import org.finos.springbot.symphony.content.CashTag;
import org.finos.springbot.symphony.content.HashTag;
import org.finos.springbot.workflow.annotations.Work;
import org.finos.springbot.workflow.content.Chat;
import org.finos.springbot.workflow.content.User;

@Work()
public class Platform {
	
	HashTag hashTag; 
	
	CashTag cashTag;
			
	User someUser;
	
	Chat chat;

	public HashTag getHashTag() {
		return hashTag;
	}

	public void setHashTag(HashTag hashTag) {
		this.hashTag = hashTag;
	}

	public CashTag getCashTag() {
		return cashTag;
	}

	public void setCashTag(CashTag cashTag) {
		this.cashTag = cashTag;
	}

	public User getSomeUser() {
		return someUser;
	}

	public void setSomeUser(User someUser) {
		this.someUser = someUser;
	}

	public Chat getChat() {
		return chat;
	}

	public void setChat(Chat chat) {
		this.chat = chat;
	}

	@Override
	public String toString() {
		return "Platform [hashTag=" + hashTag + ", cashTag=" + cashTag + ", someUser=" + someUser + ", chat=" + chat
				+ "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(cashTag, chat, hashTag, someUser);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Platform other = (Platform) obj;
		return Objects.equals(cashTag, other.cashTag) && Objects.equals(chat, other.chat)
				&& Objects.equals(hashTag, other.hashTag) && Objects.equals(someUser, other.someUser);
	}

		
}

