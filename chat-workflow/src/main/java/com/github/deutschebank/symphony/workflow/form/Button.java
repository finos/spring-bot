package com.github.deutschebank.symphony.workflow.form;

public class Button {
	
	public enum Type { ACTION, RESET };

	private String name;
	private Type type;
	private String text;
	
	public Button() {
	}
	
	public Button(String name, Type type, String text) {
		super();
		this.name = name;
		this.type = type;
		this.text = text;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

}
