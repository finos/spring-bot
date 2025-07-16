package org.finos.springbot.tool.llm;

import org.springframework.beans.factory.InitializingBean;

public interface LLMService extends InitializingBean {

	public String getApiKey();

	public void setApiKey(String apiKey);

	public String getResponse(String request);
}
