package org.finos.symphony.toolkit.workflow.response;

import org.finos.symphony.toolkit.workflow.content.Addressable;

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