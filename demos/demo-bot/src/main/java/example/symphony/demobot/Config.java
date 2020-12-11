package example.symphony.demobot;

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
