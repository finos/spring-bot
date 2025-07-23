package org.finos.springbot.tool.llm

import org.springframework.beans.factory.InitializingBean

interface LLMService : InitializingBean {

    val apiKey: String

    fun getResponse(request: String): String
}
