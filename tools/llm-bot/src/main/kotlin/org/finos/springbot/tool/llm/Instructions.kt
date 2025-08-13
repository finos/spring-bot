package org.finos.springbot.tool.llm

data class Instructions(
        val roomsUsedIn: List<String>? = null,
        val messagesPerDayLimit: Int? = null,
        val onlyAdmin: Boolean = false,
        val template: String? = null,
        val instructions: String? = null
)
