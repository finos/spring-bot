package org.finos.symphony.toolkit.workflow.content;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * This defines a Room as a collection of people.  
 * @author Rob Moffat
 *
 */
@JsonDeserialize(as = RoomDef.class)
public interface Room extends Addressable {

	public String getRoomName();
	
	public String getRoomDescription();
			
	public boolean isPub();
	
	public String getId();
}
