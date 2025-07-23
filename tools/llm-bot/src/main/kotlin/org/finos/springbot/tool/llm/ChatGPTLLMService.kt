package org.finos.springbot.tool.llm

import ai.koog.agents.core.agent.AIAgent
import ai.koog.prompt.executor.clients.openai.OpenAIModels
import ai.koog.prompt.executor.llms.all.simpleOpenAIExecutor
import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Service

@Service
class ChatGPTLLMService(override val apiKey: String) : AbstractLLMService(apiKey) {

    var agent: AIAgent<String, String>? = null

    override fun afterPropertiesSet() {
        super.afterPropertiesSet()
        agent =
                AIAgent(
                        executor = simpleOpenAIExecutor(apiKey),
                        systemPrompt =
                                "You are a helpful assistant. Answer user questions concisely.",
                        llmModel = OpenAIModels.Chat.GPT4o
                )
    }

    override fun getResponse(request: String): String = runBlocking {
        val result = agent?.run(request)
        result?.toString() ?: "No response"
    }
}
