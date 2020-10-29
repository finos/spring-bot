package org.finos.symphony.toolkit.workflow.fixture;

import org.finos.symphony.toolkit.workflow.content.Room;
import org.finos.symphony.toolkit.workflow.content.User;
import org.finos.symphony.toolkit.workflow.java.Exposed;
import org.finos.symphony.toolkit.workflow.java.Work;

@Work(name="Test Object 3", instructions="blah")
public class TestOb3 {

	private Room r;
	private User u;
	private String someText;
	
	public Room getR() {
		return r;
	}
	public void setR(Room r) {
		this.r = r;
	}
	public User getU() {
		return u;
	}
	public void setU(User u) {
		this.u = u;
	}
	public String getSomeText() {
		return someText;
	}
	public void setSomeText(String someText) {
		this.someText = someText;
	}
	
	public TestOb3() {
	}
	
	public TestOb3(Room r, User u, String someText) {
		super();
		this.r = r;
		this.u = u;
		this.someText = someText;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((r == null) ? 0 : r.hashCode());
		result = prime * result + ((someText == null) ? 0 : someText.hashCode());
		result = prime * result + ((u == null) ? 0 : u.hashCode());
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
		TestOb3 other = (TestOb3) obj;
		if (r == null) {
			if (other.r != null)
				return false;
		} else if (!r.equals(other.r))
			return false;
		if (someText == null) {
			if (other.someText != null)
				return false;
		} else if (!someText.equals(other.someText))
			return false;
		if (u == null) {
			if (other.u != null)
				return false;
		} else if (!u.equals(other.u))
			return false;
		return true;
	}
	
	@Exposed(description = "Creates a TestOb3")
	public static TestOb3 ob3(TestOb3 in) {
		return in;
	}
	
	
}
