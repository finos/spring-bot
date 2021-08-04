package org.finos.symphony.toolkit.workflow.fixture;

import org.finos.symphony.toolkit.workflow.annotations.ChatRequest;
import org.finos.symphony.toolkit.workflow.annotations.Template;
import org.finos.symphony.toolkit.workflow.annotations.Work;
import org.finos.symphony.toolkit.workflow.content.Chat;
import org.finos.symphony.toolkit.workflow.content.User;

@Work()
@Template(view="test-freemarker-view") 
public class TestTemplatedObject {

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
	
	public TestTemplatedObject() {
	}
	
	public TestTemplatedObject(Chat r, User u, String someText) {
		super();
		this.r = r;
		this.u = u;
		this.someText = someText;
	}
	
	
}
