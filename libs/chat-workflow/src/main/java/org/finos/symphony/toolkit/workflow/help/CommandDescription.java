package org.finos.symphony.toolkit.workflow.help;

import java.util.List;

import org.finos.symphony.toolkit.workflow.annotations.Work;

@Work
public class CommandDescription {
	
	private String description;
	List<String> examples;
	
	public CommandDescription(String description, List<String> examples) {
		super();
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
	
	
	
}