package org.finos.springbot.teams;

import org.finos.springbot.tests.controller.OurController;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@Configuration
public class MockTeamsConfiguration {
		
	@Bean
	public LocalValidatorFactoryBean localValidatorFactoryBean() {
		return new LocalValidatorFactoryBean();
	}
	

	@Bean
	public OurController ourController() {
		return new OurController();
	}

}