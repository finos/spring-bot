package org.finos.symphony.toolkit.workflow.fixture;

import org.finos.symphony.toolkit.workflow.actions.Template;
import org.finos.symphony.toolkit.workflow.annotations.Exposed;
import org.finos.symphony.toolkit.workflow.annotations.Work;
import org.finos.symphony.toolkit.workflow.content.Room;
import org.finos.symphony.toolkit.workflow.content.User;

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
