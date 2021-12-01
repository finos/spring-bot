package org.finos.springbot.tests.work;

import java.util.List;

import org.finos.springbot.workflow.annotations.Work;

@Work
public class CollectionSingleWork {

	List<Integer> ints;
	List<String> strings;
	
	public List<Integer> getInts() {
		return ints;
	}

	public void setInts(List<Integer> ints) {
		this.ints = ints;
	}

	public List<String> getStrings() {
		return strings;
	}

	public void setStrings(List<String> strings) {
		this.strings = strings;
	}

 }
