package org.finos.springbot.tool.llm;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import ai.koog.agents.core.agent.AIAgent;
import ai.koog.prompt.executor.clients.openai.OpenAIModels;
import ai.koog.prompt.executor.clients.openai.simpleOpenAIExecutor;
import ai.koog.prompt.executor.llms.SingleLLMPromptExecutor;
import ai.koog.prompt.executor.llms.all.SimplePromptExecutorsKt;
import ai.koog.prompt.llm.LLModel;

import org.finos.springbot.workflow.content.Message;

@Service
public class ChatGPTLLMService extends AbstractLLMService {

	@Value("${llm-bot.key}")
	String apiKey;

	AIAgent agent;

	@Override
	public void afterPropertiesSet() throws Exception {
		super.afterPropertiesSet();
		SingleLLMPromptExecutor prompt = SimplePromptExecutorsKt.simpleOpenAIExecutor(apiKey);
		LLModel model = OpenAIModels.Chat.INSTANCE.getGPT4_1();

		agent = new AIAgent<Object, Object>(
				"You are a helpful assistant. Answer user questions concisely.",
				prompt, model);
	}

	@Override
	public String getResponse(String request) {
		Object result = agent.run(request, null);
		return result.toString();
	}
}
