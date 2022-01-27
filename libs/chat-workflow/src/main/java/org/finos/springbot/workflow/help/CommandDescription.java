package org.finos.springbot.workflow.help;

import java.util.List;

import org.finos.springbot.workflow.annotations.Work;

@Work(index = false)
public class CommandDescription {

	private String description;
	private String buttonName;
	private boolean isButton;
	private List<String> examples;

	public CommandDescription(boolean isButton, String buttonName, String description, List<String> examples) {
		super();
		this.isButton = isButton;
		this.buttonName = buttonName;
		this.description = description;
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

	public List<String> getExamples() {
		return examples;
	}

	public void setExamples(List<String> examples) {
		this.examples = examples;
	}

	/**
	 * @return the isButton
	 */
	public boolean isButton() {
		return isButton;
	}

	/**
	 * @param isButton the isButton to set
	 */
	public void setButton(boolean isButton) {
		this.isButton = isButton;
	}

	/**
	 * @return the buttonName
	 */
	public String getButtonName() {
		return buttonName;
	}

	/**
	 * @param buttonName the buttonName to set
	 */
	public void setButtonName(String buttonName) {
		this.buttonName = buttonName;
	}

}