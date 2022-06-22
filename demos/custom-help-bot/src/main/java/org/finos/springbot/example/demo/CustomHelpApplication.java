package org.finos.springbot.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication
@Configuration
public class CustomHelpApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(CustomHelpApplication.class, args);
	}

}
