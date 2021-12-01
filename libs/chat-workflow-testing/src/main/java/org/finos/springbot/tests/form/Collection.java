package org.finos.springbot.tests.form;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.finos.springbot.workflow.annotations.Work;

@Work()
public class Collection {

	@Valid
	private List<Primitives> items = new ArrayList<>();

	public Collection() {
		super();
	}

	public Collection(List<Primitives> items) {
		super();
		this.items = items;
	}

	public List<Primitives> getItems() {
		return items;
	}

	public void setItems(List<Primitives> items) {
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
		Collection other = (Collection) obj;
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
