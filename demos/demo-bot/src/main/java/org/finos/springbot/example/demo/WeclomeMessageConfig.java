package org.finos.springbot.example.demo;

import org.finos.springbot.workflow.response.handlers.ResponseHandlers;
import org.finos.springbot.workflow.welcome.RoomWelcomeEventConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * This shows you how you can configure the bot to send a welcome message to users when they join a 
 * room the bot is in.  See {@link RoomWelcomeEventConsumer} for more configuration details.
 * 
 * @author rob@kite9.com
 *
 */
@Configuration
public class WeclomeMessageConfig {

	@Autowired
	ResponseHandlers rh;

	@Bean
	public RoomWelcomeEventConsumer welcomeMessages() {
		return new RoomWelcomeEventConsumer(rh);
	}
	
}
