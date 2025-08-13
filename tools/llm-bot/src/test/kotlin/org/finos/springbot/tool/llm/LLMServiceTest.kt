package org.finos.springbot.tool.llm

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
// (classes = [
// ChatWorkflowConfig::class.java,
// ])
// @ActiveProfiles(value = ["symphony"])
class LLMServiceTest {

    @Autowired lateinit var llmService: LLMService

    @Test
    fun testLLMServiceResponds() {
        val response = llmService.getResponse("Hello, how are you?")
        Assertions.assertNotNull(response)
        Assertions.assertTrue(response.length > 0)
    }
}
