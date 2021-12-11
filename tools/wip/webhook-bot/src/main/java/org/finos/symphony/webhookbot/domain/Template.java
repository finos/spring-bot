package org.finos.symphony.webhookbot.domain;

import org.finos.symphony.toolkit.workflow.java.Work;

public class Template {
	private String name;
	private String contents;
	private boolean shared;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getContents() {
		return contents;
	}
	public void setContents(String contents) {
		this.contents = contents;
	}
	public boolean isShared() {
		return shared;
	}
	public void setShared(boolean shared) {
		this.shared = shared;
	}

}
