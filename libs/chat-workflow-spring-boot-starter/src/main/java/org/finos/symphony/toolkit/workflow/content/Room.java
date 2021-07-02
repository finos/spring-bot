package org.finos.symphony.toolkit.workflow.content;

/**
 * This defines a Room as a collection of people. 
 *  
 * @author Rob Moffat
 *
 */
public interface Room extends Addressable {

	public String getRoomName();
	
	public String getRoomDescription();
			
	public boolean isPub();
	
	public String getId();
}
