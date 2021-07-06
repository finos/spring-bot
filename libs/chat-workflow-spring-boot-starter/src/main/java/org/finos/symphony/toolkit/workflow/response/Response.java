package org.finos.symphony.toolkit.workflow.response;

import org.finos.symphony.toolkit.workflow.content.Addressable;
import org.finos.symphony.toolkit.workflow.response.handlers.ResponseHandler;

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

	/**
	 * An optional template name, which will be used for customizing the formatting of the
	 * response (depending on the output channel).  If the template cannot be resolved 
	 * given the name, the {@link ResponseHandler} will fallback to a default.
	 */
	public String getTemplateName();
	
}