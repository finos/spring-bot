package com.github.deutschebank.symphony.workflow.fixture;

import com.github.deutschebank.symphony.workflow.content.Room;
import com.github.deutschebank.symphony.workflow.content.User;
import com.github.deutschebank.symphony.workflow.java.Exposed;
import com.github.deutschebank.symphony.workflow.java.Work;
import com.github.deutschebank.symphony.workflow.sources.symphony.Template;

@Work(name="Test Templated", instructions="blah")
@Template(view="classpath:/test-freemarker-view.ftl") 
public class TestTemplatedObject {

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
	
	public TestTemplatedObject() {
	}
	
	public TestTemplatedObject(Room r, User u, String someText) {
		super();
		this.r = r;
		this.u = u;
		this.someText = someText;
	}
	
	
	@Exposed(description = "Creates a templated object")
	public static TestTemplatedObject templated(TestTemplatedObject in) {
		return in;
	}
	
	
}
