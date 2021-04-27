package org.finos.symphony.webhookbot.domain;

import org.finos.symphony.toolkit.workflow.java.Work;

@Work(editable = false, instructions = "Create a new template for a webhook", name="New Template")
@org.finos.symphony.toolkit.workflow.sources.symphony.Template(edit="classpath:/templates/TemplateEdit.ftl")
public final class Template {

	String name;
	
	Boolean shared = false;
	
	String contents;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Boolean getShared() {
		return shared;
	}

	public void setShared(Boolean shared) {
		this.shared = shared;
	}

	public String getContents() {
		return contents;
	}

	public void setContents(String contents) {
		this.contents = contents;
	}

	@Override
	public String toString() {
		return "[" + name + "]";
	}
	
	
}
