package org.finos.springbot.symphony.json;

import org.finos.springbot.workflow.annotations.Work;
import org.finos.springbot.workflow.content.Chat;
import org.finos.springbot.workflow.content.User;

@Work(jsonTypeName = {"org.finos.symphony.toolkit.workflow.fixture.eJTestObject"})
public class EJTestObject {

	private Chat r;
	private User u;
	private String someText;
	
	public Chat getR() {
		return r;
	}
	public void setR(Chat r) {
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
	
	public EJTestObject() {
	}
	
	public EJTestObject(Chat r, User u, String someText) {
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
		EJTestObject other = (EJTestObject) obj;
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
	
	
}
