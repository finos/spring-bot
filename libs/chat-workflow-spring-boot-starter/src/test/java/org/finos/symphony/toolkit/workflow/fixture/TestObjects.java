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
	
	
	@Override
	public String toString() {
		return "TestObjects [items=" + items + "]";
	}
	
}
