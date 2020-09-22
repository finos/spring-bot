package com.github.deutschebank.symphony.workflow.fixture;

import com.github.deutschebank.symphony.workflow.content.Author;
import com.github.deutschebank.symphony.workflow.content.ID;
import com.github.deutschebank.symphony.workflow.content.User;
import com.github.deutschebank.symphony.workflow.java.Exposed;
import com.github.deutschebank.symphony.workflow.java.Work;

@Work(name="Test Object 4", instructions="sundry other fields")
public class TestOb4 {

	public enum Choice { A, B, C };
	
	ID theId = new ID();
	
	Choice c;
	
	boolean b;
	
	Author a= Author.CURRENT_AUTHOR.get();
	
	User someUser;
	
	public TestOb4() {
		super();
	}

	public TestOb4(Choice c, boolean b, Author a, User someUser) {
		super();
		this.c = c;
		this.b = b;
		this.a = a;
		this.someUser = someUser;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((a == null) ? 0 : a.hashCode());
		result = prime * result + (b ? 1231 : 1237);
		result = prime * result + ((c == null) ? 0 : c.hashCode());
		result = prime * result + ((someUser == null) ? 0 : someUser.hashCode());
		return result;
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
		if (a == null) {
			if (other.a != null)
				return false;
		} else if (!a.equals(other.a))
			return false;
		if (b != other.b)
			return false;
		if (c != other.c)
			return false;
		if (someUser == null) {
			if (other.someUser != null)
				return false;
		} else if (!someUser.equals(other.someUser))
			return false;
		return true;
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

	public Author getA() {
		return a;
	}

	public void setA(Author a) {
		this.a = a;
	}

	public User getSomeUser() {
		return someUser;
	}

	public void setSomeUser(User someUser) {
		this.someUser = someUser;
	}

	public ID getTheId() {
		return theId;
	}

	public void setTheId(ID theId) {
		this.theId = theId;
	}

	@Exposed(description = "Creates a TestOb4")
	public static TestOb4 ob4(TestOb4 in) {
		return in;
	}
	
}
