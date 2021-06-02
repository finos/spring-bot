package org.finos.symphony.toolkit.workflow.fixture;

import org.finos.symphony.toolkit.workflow.annotations.ChatVariable;
import org.finos.symphony.toolkit.workflow.content.CodeBlock;
import org.finos.symphony.toolkit.workflow.content.Message;
import org.finos.symphony.toolkit.workflow.content.PastedTable;
import org.finos.symphony.toolkit.workflow.content.Room;
import org.finos.symphony.toolkit.workflow.content.User;
import org.finos.symphony.toolkit.workflow.form.FormSubmission;
import org.finos.symphony.toolkit.workflow.java.Exposed;
import org.springframework.stereotype.Controller;

@Controller
public class OurController {
	
	// todo: do we need any other kinds of wildcards?
	@Exposed("*") 
	public void listenToEverything(Message m) {
		// guarav's reminder bot should do this - it needs to parse the date out of every
		// message
	}
	
	@Exposed(value = "call", formClass=Person.class)
	public void callPerson(Person arg) {
		// do your own form processing
	}
	
	@Exposed(value = "new claim", isButton = true)
	public void startNewClaim(StartClaim sc) {
		// can't run without StartClaim, returns form to begin a process..
		// user fills it in and this runs.
	}
	

	@Exposed(value = "process", isButton = true, formName = "process-form")
	public void processForm(FormSubmission f) {
		// do your own form processing
		// is this needed?
	}
	
	
	@Exposed("list") 
	public void doCommand(Message m) {
		// do something when user types in "/list"
	}

	
	@Exposed("show {user}") 
	public void userDetails(@ChatVariable("user") User u) {
		// provide some user details, e.g. /show @Rob Moffat
	}
	

	@Exposed("process {sometable} {user}") 
	public void process(@ChatVariable("sometable") PastedTable t, @ChatVariable(required = false, value="user") User u) {
		// provide some processing for a table.
	}
	

	@Exposed("update {code}") 
	public void process(@ChatVariable("code") CodeBlock cb) {
		// provide some processing for a block of code
	}
	
	@Exposed({
		"add {user} to {room}", 
		"add {user} {room}"}) 
	public void addUserToRoom(@ChatVariable("user") User u, @ChatVariable("room") Room r) {
		// provide some processing for a block of code
	}
	
	
	@Exposed(admin = true, value = "delete {user}")
	public void removeUserFromRoom(@ChatVariable("user") User u, Room r) {
		
	}
	

}
