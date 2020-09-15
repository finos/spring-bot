package com.github.deutschebank.symphony.workflow.fixture;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import com.github.deutschebank.symphony.workflow.java.Exposed;
import com.github.deutschebank.symphony.workflow.java.Work;

@Work(editable=true, instructions="basket of stuff", name = "List of Test Objects")
public class TestObjects {

	@Valid
	private List<TestObject> items;

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
	
	@Exposed
	public TestObjects remove(int i) {
		items.remove(i);
		return this;
	}
	
	@Exposed(description= "creates a new test objects")
	public static TestObjects testObjects() {
		return TestWorkflowConfig.INITIAL;
	}
	
	@Exposed(description = "Add another test object")
	public TestObjects add(TestObject o) {
		List<TestObject> chg = new ArrayList<>(items);
		chg.add(o);
		return new TestObjects(chg);
	}

	@Override
	public String toString() {
		return "TestObjects [items=" + items + "]";
	}
	
}
