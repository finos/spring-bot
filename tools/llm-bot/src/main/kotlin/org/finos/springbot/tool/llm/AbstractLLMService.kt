package org.finos.springbot.tool.llm

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.ResourceLoader

abstract class AbstractLLMService : LLMService, InitializingBean {

    protected var instructions: Instructions? = null

    @Autowired lateinit var resourceLoader: ResourceLoader

    override fun afterPropertiesSet() {
        val resource = resourceLoader.getResource("classpath:instructions.json")
        val objectMapper = ObjectMapper()
        instructions = objectMapper.readValue(resource.inputStream, Instructions::class.java)
        println(instructions)
    }
}
