package org.finos.symphony.toolkit.workflow.fixture;

import org.finos.symphony.toolkit.workflow.annotations.ChatVariable;
import org.finos.symphony.toolkit.workflow.content.CodeBlock;
import org.finos.symphony.toolkit.workflow.content.Message;
import org.finos.symphony.toolkit.workflow.content.PastedTable;
import org.finos.symphony.toolkit.workflow.content.Room;
import org.finos.symphony.toolkit.workflow.content.User;
import org.finos.symphony.toolkit.workflow.form.FormSubmission;
import org.finos.symphony.toolkit.workflow.java.Exposed;

public class OurController {

	
	
	@Exposed(value = "ok", isButton = true, formName="someFormName")
	public void processForm(FormSubmission f) {
		// do your own form processing
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
	
	

}
