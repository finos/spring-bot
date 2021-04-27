package org.finos.symphony.webhookbot.domain;

import org.finos.symphony.toolkit.workflow.java.Work;

@Work(editable = true, instructions = "Decide which webhooks are reported in Symphony")
public class Filter {

	enum Type { INCLUDE, EXCLUDE }
	
	enum Part { HEADER, BODY }
	
	private Type type;
	
	private Part part;
	
	private String key;
	
	private String value;

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public Part getPart() {
		return part;
	}

	public void setPart(Part part) {
		this.part = part;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	
}
