package org.finos.springbot.symphony.form;

import java.util.Objects;

import org.finos.springbot.workflow.annotations.Work;
import org.finos.springbot.workflow.content.User;
import org.finos.symphony.toolkit.workflow.sources.symphony.content.CashTag;
import org.finos.symphony.toolkit.workflow.sources.symphony.content.HashTag;

@Work()
public class Platform {
	
	HashTag hashTag; 
	
	CashTag cashTag;
			
	User someUser;

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

	@Override
	public int hashCode() {
		return Objects.hash(cashTag, hashTag, someUser);
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
		return Objects.equals(cashTag, other.cashTag) && Objects.equals(hashTag, other.hashTag)
				&& Objects.equals(someUser, other.someUser);
	}

	@Override
	public String toString() {
		return "Platform [hashTag=" + hashTag + ", cashTag=" + cashTag + ", someUser=" + someUser + "]";
	}
	
}

