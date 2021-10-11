package org.finos.symphony.toolkit.workflow.fixture;

import java.util.Objects;

import org.finos.springbot.sources.teams.content.CashTag;
import org.finos.springbot.sources.teams.content.HashTag;
import org.finos.symphony.toolkit.workflow.annotations.Work;
import org.finos.symphony.toolkit.workflow.content.User;

@Work()
public class WeirdObject {

	public enum Choice { A, B, C };
	
	HashTag theId = HashTag.createID();
	
	CashTag cashTag = new CashTag("cashmoney");
	
	Choice c;
	
	boolean b;
		
	User someUser;
	
	public WeirdObject() {
		super();
	}

	public WeirdObject(Choice c, boolean b, User someUser) {
		super();
		this.c = c;
		this.b = b;
		this.someUser = someUser;
	}

	
	public Choice getC() {
		return c;
	}

	public void setC(Choice c) {
		this.c = c;
	}

	public boolean isB() {
		return b;
	}

	public void setB(boolean b) {
		this.b = b;
	}

	public User getSomeUser() {
		return someUser;
	}

	public void setSomeUser(User someUser) {
		this.someUser = someUser;
	}

	public HashTag getTheId() {
		return theId;
	}

	public void setTheId(HashTag theId) {
		this.theId = theId;
	}

	public CashTag getCashTag() {
		return cashTag;
	}

	public void setCashTag(CashTag cashTag) {
		this.cashTag = cashTag;
	}

	@Override
	public int hashCode() {
		return Objects.hash(b, c, cashTag, someUser, theId);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		WeirdObject other = (WeirdObject) obj;
		return b == other.b && c == other.c && Objects.equals(cashTag, other.cashTag)
				&& Objects.equals(someUser, other.someUser) && Objects.equals(theId, other.theId);
	}
	
	
	
}

