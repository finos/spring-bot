package org.finos.springbot.workflow.form;

import java.util.LinkedHashMap;
import java.util.Map;

import org.finos.springbot.workflow.annotations.Work;

@Work(index = false)
public class ErrorMap {
	
	private Map<String, String> contents;

	public Map<String, String> getContents() {
		return contents;
	}

	public void setContents(Map<String, String> contents) {
		this.contents = contents;
	}

	public ErrorMap() {
		this.contents = new LinkedHashMap<>();
	}

	public ErrorMap(Map<String, String> arg0) {
		this.contents = arg0;
	}

	public void add(String field, String error) {
		contents.put(field, error);
	}

	public int size() {
		return contents.size();
	}

	
}
