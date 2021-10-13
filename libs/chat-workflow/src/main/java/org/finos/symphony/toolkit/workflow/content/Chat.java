package org.finos.symphony.toolkit.workflow.content;

/**
 * A chat is some named place where you can send messages, on a one-to-many basis.  
 *  
 * @author Rob Moffat
 *
 */
public interface Chat extends Addressable {

	public String getName();
	
}
