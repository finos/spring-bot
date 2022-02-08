package org.finos.springbot.tests.controller;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.finos.springbot.workflow.annotations.Work;

@Work()
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
	public String toString() {
		return "TestObjects [items=" + items + "]";
	}
}
	
