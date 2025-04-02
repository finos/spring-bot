package org.finos.springbot.tool.llm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LLMBotApp {
	
	public static void main(String[] args) {
		SpringApplication.run(new Class[] { LLMBotApp.class }, args);

	}
}
