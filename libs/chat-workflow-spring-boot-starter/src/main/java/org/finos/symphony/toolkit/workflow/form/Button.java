package org.finos.symphony.toolkit.workflow.form;

import org.finos.symphony.toolkit.workflow.annotations.Work;

@Work
public class Button {
	
	public enum Type { ACTION, RESET };

	private String name;
	private Type buttonType;
	private String text;
	
	public Button() {
	}
	
	public Button(String name, Type type, String text) {
		super();
		this.name = name;
		this.buttonType = type;
		this.text = text;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Type getButtonType() {
		return buttonType;
	}

	public void setButtonType(Type type) {
		this.buttonType = type;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

}
