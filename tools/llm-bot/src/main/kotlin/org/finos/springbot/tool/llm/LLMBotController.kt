package org.finos.springbot.tool.llm

import org.finos.springbot.workflow.annotations.ChatRequest
import org.finos.springbot.workflow.annotations.WorkMode
import org.finos.springbot.workflow.content.Chat
import org.finos.springbot.workflow.content.Message
import org.finos.springbot.workflow.content.User
import org.finos.springbot.workflow.response.Response
import org.finos.springbot.workflow.response.WorkResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller

@Controller
class LLMBotController @Autowired constructor(private val llmService: LLMService) {

    @ChatRequest("*")
    fun respondInRoom(author: User, m: Message, r: Chat): Response {
        // take the contents of the message and send in to the LLM for processing.
        // look up instructions for chatRoom.
        // template the request
        val content = llmService.getResponse(m.text)
        val out = WorkResponse(r, content, WorkMode.VIEW)
        return out
    }
}
