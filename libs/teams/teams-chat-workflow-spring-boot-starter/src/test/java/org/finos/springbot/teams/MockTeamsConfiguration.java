package org.finos.springbot.teams;

import org.finos.springbot.teams.conversations.MockSpringBotMicrosoftAppCredentials;
import org.finos.springbot.teams.conversations.SpringBotAppCredentials;
import org.finos.springbot.tests.controller.OurController;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import com.microsoft.bot.connector.authentication.CertificateAppCredentials;

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

	@Bean
	@Primary
	public SpringBotAppCredentials dummyMicrosoftCredentials() {
		return new MockSpringBotMicrosoftAppCredentials();
	}

	@Bean
	public CertificateAppCredentials certificateAppCredentials(SpringBotAppCredentials appCredentials) {
		return appCredentials.getAppCredentials();
	}

}