package org.finos.springbot.tool.llm

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication class LLMBotApp

fun main(args: Array<String>) {
    SpringApplication.run(LLMBotApp::class.java, *args)
}
