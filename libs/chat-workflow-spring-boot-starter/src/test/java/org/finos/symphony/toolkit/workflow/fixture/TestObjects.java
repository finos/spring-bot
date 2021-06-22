package org.finos.symphony.toolkit.workflow.fixture;

import org.finos.symphony.toolkit.workflow.content.Word;
import org.finos.symphony.toolkit.workflow.java.Exposed;
import org.finos.symphony.toolkit.workflow.java.Work;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Work(editable=true, instructions="basket of stuff", name = "List of Test Objects")
public class TestObjects {

	@Valid
	private List<TestObject> items = new ArrayList<>();

	public TestObjects() {
		super();
	}

	public TestObjects(List<TestObject> items) {
		super();
		this.items = items;
	}

	public List<TestObject> getItems() {
		return items;
	}

	public void setItems(List<TestObject> items) {
		this.items = items;
	}
	
	@Exposed
	public TestObjects show() {
		return this;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((items == null) ? 0 : items.hashCode());
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
		TestObjects other = (TestObjects) obj;
		if (items == null) {
			if (other.items != null)
				return false;
		} else if (!items.equals(other.items))
			return false;
		return true;
	}
	
	@Exposed(description="removes item by number. e.g. /remove 4")
	public TestObjects remove(Word w1, Word w2) {
		Integer i = Integer.parseInt(w2.getText());
		items.remove((int) i);
		return this;
	}
	
	@Exposed(description= "creates a new test objects")
	public static TestObjects testObjects() {
		return TestWorkflowConfig.INITIAL_TEST_OBJECTS;
	}
	
	@Exposed(description = "Add another test object")
	public TestObjects add(TestObject o) {
		List<TestObject> chg = new ArrayList<>(items);
		chg.add(o);
		return new TestObjects(chg);
	}

	@Exposed(description="Throwable exception thrown")
	public TestObjects throwable(Word w1, Word w2) throws Throwable {
		throw new Throwable("Throwable exception thrown");
	}

	@Exposed(description="Exception exception thrown")
	public TestObjects exception(Word w1, Word w2) throws Throwable {
		throw new Exception("Exception exception thrown");
	}

	@Exposed(description="RuntimeException exception thrown")
	public TestObjects runtime(Word w1, Word w2) throws Throwable {
		throw new RuntimeException("RuntimeException exception thrown");
	}

	@Exposed(description="Exception thrown with no message")
	public TestObjects exceptionnomessage(Word w1, Word w2) throws Throwable {
		throw new Exception();
	}
	@Exposed(description="Exception thrown with no exception details")
	public TestObjects exceptionnullcause(Word w1, Word w2) throws Throwable {
		throw new Throwable(null, null);
	}

	@Exposed(description="Null pointer exception thrown")
	public TestObjects exceptionnullpointercause(Word w1, Word w2) throws Exception {
		Exception causeException = new Exception();
		throw new Exception("Null pointer exception thrown", causeException);
	}
	
	@Override
	public String toString() {
		return "TestObjects [items=" + items + "]";
	}
	
}
