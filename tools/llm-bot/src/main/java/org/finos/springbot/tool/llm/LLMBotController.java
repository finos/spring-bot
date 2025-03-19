package org.finos.springbot.tool.llm;

import org.finos.springbot.example.rooms.Broadcast;
import org.finos.springbot.workflow.annotations.ChatRequest;
import org.finos.springbot.workflow.annotations.ChatResponseBody;
import org.finos.springbot.workflow.annotations.WorkMode;
import org.springframework.stereotype.Controller;

@Controller
public class LLMBotController {

	@ChatRequest
	public void respondInRoom(@Address chatRoom, Message m, Room r) { 
	  // take the contents of the message and send in to the LLM for processing.  
	  // look up instructions for chatRoom.
	  // template the request
	}
	
	
	@ChatRequest(value = "broadcast")
	@ChatResponseBody(workMode = WorkMode.EDIT)
	public Broadcast createForm() {
		return new Broadcast();
	}
}
