package org.finos.springbot.tool.llm;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class AbstractLLMService implements LLMService, InitializingBean {


    protected Instructions instructions;

	@Autowired
	private ResourceLoader resourceLoader;

	@Override
	public void afterPropertiesSet() throws Exception {
		Resource resource = resourceLoader.getResource("classpath:instructions.json");
		ObjectMapper objectMapper = new ObjectMapper();
		Instructions instructions = objectMapper.readValue(resource.getInputStream(), Instructions.class);
		System.out.println(instructions);
        this.instructions = instructions;
	}
}