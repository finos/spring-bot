package org.finos.symphony.toolkit.workflow.help;

import java.util.List;

import org.finos.symphony.toolkit.workflow.annotations.Work;

@Work
public interface CommandDescription {
		
	public String getDescription();
	
	public List<String> getExamples();

	/**
	 * Whether this method can be exposed as a button
	 */
	boolean isButton();
	
	/**
	 * Whether this method can be called by typing it's name.
	 */
	boolean isMessage();
	
}