package org.finos.springbot.tool.llm;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class LLMServiceTest {

    @Test
    public void testLLMServiceResponds() throws Exception {

        LLMService llmService = new ChatGPTLLMService("some-key");

        llmService.afterPropertiesSet();
        String response = llmService.getResponse("Hello, how are you?");
        Assertions.assertNotNull(response);
        Assertions.assertTrue(response.length() > 0);

    }

}
