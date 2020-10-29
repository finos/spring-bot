package org.finos.symphony.toolkit.workflow;

import java.util.List;

import org.finos.symphony.toolkit.workflow.content.Addressable;
import org.finos.symphony.toolkit.workflow.content.Room;
import org.finos.symphony.toolkit.workflow.content.User;
import org.finos.symphony.toolkit.workflow.form.ButtonList;

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
		
	public String getNamespace();
	
	public List<CommandDescription> getCommands(Addressable r);
	
	public boolean hasMatchingCommand(String name, Addressable r);
		
	public ButtonList gatherButtons(Object out, Addressable r);
	
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

	public String getInstructions(Class<?> c);

	public String getName(Class<?> c);

}

