package com.github.deutschebank.symphony.workflow;

import java.util.List;

import com.github.deutschebank.symphony.workflow.content.Message;
import com.github.deutschebank.symphony.workflow.content.Room;
import com.github.deutschebank.symphony.workflow.content.User;
import com.github.deutschebank.symphony.workflow.form.Button;
import com.github.deutschebank.symphony.workflow.history.History;
import com.github.deutschebank.symphony.workflow.response.Response;
import com.github.deutschebank.symphony.workflow.room.Rooms;

/**
 * A workflow is a collection of steps, which can be triggered by messages posted in a Symphony room.
 * 
 * @author Rob Moffat
 *
 */
public interface Workflow {
	
	public static interface CommandDescription {
		
		public String getName();
		
		public String getDescription();
		
		public boolean isShowButton();
		
		public boolean isShowText();
		
	}
		
	public String getNamespace();
	
	public List<CommandDescription> getCommands(Room r);
	
	public boolean hasMatchingCommand(String name, Room r);
	
	public List<Response> applyCommand(User u, Room r, String commandName, Object argument, Message m);
	
	List<Button> gatherButtons(Object out, Room r);
	
	public Rooms getRoomsApi();
	
	public History getHistoryApi();
	
	/**
	 * Important named rooms that must exist for the workflow.  
	 */
	public List<Room> getKeyRooms();
	
	/**
	 * List of administrators to be set on any rooms that get created.
	 */
	public List<User> getAdministrators();
	
	/**
	 * This is the set of classes that will be sent in the "data" payload of the messages used to communicate workflow.
	 */
	public List<Class<?>> getDataTypes();

	public void registerHistoryProvider(History h);
	
	public void registerRoomsProvider(Rooms r);
}

