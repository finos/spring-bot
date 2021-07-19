package org.finos.symphony.toolkit.workflow.help;

import java.util.List;

import org.finos.symphony.toolkit.workflow.annotations.Work;

@Work
public class CommandDescription {
	
	private String description;
	boolean button;
	boolean message;
	List<String> examples;
	
	public CommandDescription(String description, boolean button, boolean message, List<String> examples) {
		super();
		this.description = description;
		this.button = button;
		this.message = message;
		this.examples = examples;
	}

	public CommandDescription() {
		super();
	}
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public boolean isButton() {
		return button;
	}
	public void setButton(boolean button) {
		this.button = button;
	}
	public boolean isMessage() {
		return message;
	}
	public void setMessage(boolean message) {
		this.message = message;
	}
	public List<String> getExamples() {
		return examples;
	}
	public void setExamples(List<String> examples) {
		this.examples = examples;
	}
	
	
	
}