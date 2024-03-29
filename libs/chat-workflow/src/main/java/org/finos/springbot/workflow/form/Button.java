package org.finos.springbot.workflow.form;

import org.finos.springbot.workflow.annotations.Work;

@Work(index = false)
public class Button implements Comparable<Button> {
	
	public enum Type { ACTION, RESET };

	private String name;
	private Type buttonType;
	private String text;
	
	public Button() {
	}
	
	public Button(Class<?> c, String methodName, Type t, String text) {
		this(c.getName()+"-"+methodName, t, text);
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

	@Override
	public int compareTo(Button o) {
		return this.getText().compareTo(o.getText());
	}

}
