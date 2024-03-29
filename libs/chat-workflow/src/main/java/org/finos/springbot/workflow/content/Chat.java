package org.finos.springbot.workflow.content;

/**
 * A chat is some named place where you can send messages, on a one-to-many basis.  
 *  
 * @author Rob Moffat
 *
 */
public interface Chat extends Addressable {

	/**
	 * Human-readable identifier for chat
	 */
	public String getName();

}
