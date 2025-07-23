package org.finos.springbot.tool.llm;

import org.springframework.stereotype.Service;

@Service
public class ChatGPTLLMService2 extends AbstractLLMService {

    String apiKey;

    @Override
    public String getResponse(String request) {
        return "Hello, world!";
    }

    @Override
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    @Override
    public String getApiKey() {
        return apiKey;
    }
}
