package org.finos.springbot.tool.llm;

import org.finos.springbot.workflow.annotations.ChatRequest;
import org.finos.springbot.workflow.annotations.WorkMode;
import org.finos.springbot.workflow.content.Chat;
import org.finos.springbot.workflow.content.Message;
import org.finos.springbot.workflow.content.User;
import org.finos.springbot.workflow.response.Response;
import org.finos.springbot.workflow.response.WorkResponse;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class LLMBotController {

	@Autowired
	ChatGPTLLMService2 llmService;

	@ChatRequest("*")
	public Response respondInRoom(User author, Message m, Chat r) {
		// take the contents of the message and send in to the LLM for processing.
		// look up instructions for chatRoom.
		// template the request
		String content = llmService.getResponse(m.getText());
		WorkResponse out = new WorkResponse(r, content, WorkMode.VIEW);
		return out;
	}

}
