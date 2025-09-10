package org.finos.springbot.tool.llm;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class LLMServiceTest {

    @Autowired
    LLMService llmService;

    @Test
    public void testLLMServiceResponds() {
        String response = llmService.getResponse("Hello, how are you?");
        Assertions.assertNotNull(response);
        Assertions.assertTrue(response.length() > 0);
        System.out.println("RESPONSE: " + response);
    }
}
