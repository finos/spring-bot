package org.finos.symphony.toolkit.workflow.fixture;

import java.util.Objects;

import org.finos.symphony.toolkit.workflow.annotations.Work;
import org.finos.symphony.toolkit.workflow.content.User;
import org.finos.symphony.toolkit.workflow.sources.symphony.content.HashTag;

@Work()
public class TestOb4 {

	public enum Choice { A, B, C };
	
	HashTag theId = HashTag.createID();
	
	Choice c;
	
	boolean b;
		
	User someUser;
	
	public TestOb4() {
		super();
	}

	public TestOb4(Choice c, boolean b, User someUser) {
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

	@Override
	public int hashCode() {
		return Objects.hash(b, c, someUser, theId);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TestOb4 other = (TestOb4) obj;
		return b == other.b && c == other.c && Objects.equals(someUser, other.someUser)
				&& Objects.equals(theId, other.theId);
	}
	
	
}

