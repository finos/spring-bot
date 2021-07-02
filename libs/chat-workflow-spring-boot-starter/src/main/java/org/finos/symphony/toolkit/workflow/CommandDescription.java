package org.finos.symphony.toolkit.workflow;

public interface CommandDescription {
	
	public String getName();
	
	public String getDescription();
	
	boolean addToHelp();
	
	/**
	 * Whether this method can be exposed as a button
	 */
	boolean isButton();
	
	/**
	 * Whether this method can be called by typing it's name.
	 */
	boolean isMessage();
	
}