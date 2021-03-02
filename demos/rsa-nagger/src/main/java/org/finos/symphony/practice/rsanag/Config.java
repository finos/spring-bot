package org.finos.symphony.practice.rsanag;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class Config {
	
	@Bean
	public  ObjectMapper objectMapper() {
		return new ObjectMapper();
	}
		
}
