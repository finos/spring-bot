package org.finos.springbot.workflow.response;

import org.finos.springbot.workflow.content.Addressable;

/**
 * A general response to an addressable destination.  
 * 
 * @author rob@kite9.com
 *
 */
public interface Response {
	
	/**
	 * Where the response will be sent.
	 */
	public Addressable getAddress();
	
}