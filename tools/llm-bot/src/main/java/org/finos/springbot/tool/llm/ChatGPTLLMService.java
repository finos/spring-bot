package org.finos.springbot.tool.llm;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChatGPTLLMService implements LLMService {

    @Autowired
    ChatModel chatModel;

    @Override
    public String getResponse(String request) {
        ChatResponse response = chatModel.call(
                new Prompt(
                        request,
                        OpenAiChatOptions.builder()
                                .model("gpt-4o")
                                .temperature(0.4)
                                .build()));
        return response.toString();
    }
}
