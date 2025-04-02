package org.finos.springbot.tool.llm;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;

import com.unfbx.chatgpt.OpenAiClient;
import com.unfbx.chatgpt.entity.chat.ChatCompletion;
import com.unfbx.chatgpt.entity.chat.ChatCompletionResponse;
import com.unfbx.chatgpt.entity.chat.Message;

public class ChatGPTLLMService implements LLMService, InitializingBean {

	OpenAiClient openAiClient;

	@Value("llm-bot.key")
	String theKey;

	@Override
	public void afterPropertiesSet() throws Exception {
		openAiClient = OpenAiClient.builder().apiKey(Arrays.asList(theKey)).build();
	}

	@Override
	public String getResponse(String request) {
		Message message = Message.builder().role(Message.Role.USER).content(request).build();
		ChatCompletion chatCompletion = ChatCompletion.builder().messages(Arrays.asList(message)).build();
		ChatCompletionResponse chatCompletionResponse = openAiClient.chatCompletion(chatCompletion);
		String out = chatCompletionResponse.getChoices().stream().map(e -> {
			return e.getMessage().getContent();
		}).collect(Collectors.joining("\n"));
		return out;
	}

}
