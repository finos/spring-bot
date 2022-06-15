package org.finos.springbot.example.rooms;

import org.finos.springbot.workflow.annotations.ChatButton;
import org.finos.springbot.workflow.annotations.ChatRequest;
import org.finos.springbot.workflow.annotations.ChatResponseBody;
import org.finos.springbot.workflow.annotations.WorkMode;
import org.finos.springbot.workflow.content.Message;
import org.finos.springbot.workflow.response.MessageResponse;
import org.springframework.stereotype.Controller;

@Controller
public class BroadcastController {

	@ChatRequest(value = "broadcast")
	@ChatResponseBody(workMode = WorkMode.EDIT)
	public Broadcast createForm() {
		return new Broadcast();
	}
	
	@ChatButton(value = Broadcast.class, buttonText = "Broadcast", showWhen = WorkMode.EDIT) 
	public MessageResponse broadcast(Broadcast br) {
		Message out = Message.of(br.send);
		return new MessageResponse(br.to, out);
	}
}
