package org.finos.springbot.symphony.json;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.finos.springbot.workflow.annotations.Work;

@Work()
public class TestBeans {

	@Valid
	private List<TestBean> items = new ArrayList<>();

	public TestBeans() {
		super();
	}

	public TestBeans(List<TestBean> items) {
		super();
		this.items = items;
	}

	public List<TestBean> getItems() {
		return items;
	}

	public void setItems(List<TestBean> items) {
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
		TestBeans other = (TestBeans) obj;
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
