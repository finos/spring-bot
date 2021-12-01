package org.finos.springbot.workflow.content;

public interface Addressable {
	
	/**
	 * A system identifier (address) for delivery of messages 
	 * and uniquely identifying this addressable.
	 */
	public String getKey();

}
