package org.finos.springbot.tool.llm;

import java.time.Clock;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import ai.koog.agents.core.agent.AIAgent;
import ai.koog.agents.core.agent.config.AIAgentConfig;
import ai.koog.agents.core.agent.config.AIAgentConfigBase;
import ai.koog.agents.core.agent.entity.AIAgentStrategy;
import ai.koog.agents.core.tools.ToolRegistry;
import ai.koog.agents.ext.agent.AIAgentStrategiesKt;
import ai.koog.prompt.dsl.Prompt;
import ai.koog.prompt.executor.clients.openai.OpenAIModels;
import ai.koog.prompt.executor.llms.SingleLLMPromptExecutor;
import ai.koog.prompt.executor.llms.all.SimplePromptExecutorsKt;
import ai.koog.prompt.llm.LLModel;
import kotlin.jvm.JvmClassMappingKt;
import kotlin.reflect.KClass;
import kotlin.reflect.KType;
import kotlin.reflect.full.KClasses;
import kotlin.reflect.jvm.ReflectJvmMapping;
import kotlin.reflect.jvm.internal.impl.descriptors.runtime.components.ReflectKotlinClass;

@Service
public class ChatGPTLLMService extends AbstractLLMService {

	@Value("${llm-bot.key}")
	String apiKey;

	public String getApiKey() {
		return apiKey;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	AIAgent<String, String> agent;

	@Override
	public void afterPropertiesSet() throws Exception {
		super.afterPropertiesSet();

		SingleLLMPromptExecutor prompt = SimplePromptExecutorsKt.simpleOpenAIExecutor(apiKey);
		LLModel model = OpenAIModels.Chat.INSTANCE.getGPT4_1();
		AIAgentStrategy strategy = AIAgentStrategiesKt.chatAgentStrategy();

		AIAgentConfigBase config = AIAgentConfig.Companion.withSystemPrompt("You are a helpful assistant.", model,
				"llm-example", 10);

		KClass<String> stringKClass = JvmClassMappingKt.getKotlinClass(String.class);
		KType stringKType = KClasses.createType(stringKClass);

		ToolRegistry registry = null;

		Clock clock = Clock.systemUTC();

		agent = new AIAgent<String, String>(null, null, null, null, null, null, null, null, null);
																				stringKClass,
				stringKClass,
				prompt,
				strategy,
				config,
				registry,
				clock,
				(f) -> {
				});
	}

	@Override
	public String getResponse(String request) {
		Object result = agent.run(request, null);
		return result.toString();
	}
}
