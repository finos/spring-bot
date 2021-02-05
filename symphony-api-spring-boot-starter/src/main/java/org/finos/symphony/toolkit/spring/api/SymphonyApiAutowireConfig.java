package org.finos.symphony.toolkit.spring.api;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.symphony.api.agent.MessagesApi;
import com.symphony.api.pod.SystemApi;

/**
 * If you have wired up a single bot identity in your app (under symphony.bot.identity in your application.yml) then
 * we create beans for all of the symphony APIs so that you can autowire them.
 * 
 * @author moffrob
 *
 */
@Configuration
@AutoConfigureAfter({SymphonyApiConfig.class})
public class SymphonyApiAutowireConfig {

	@Bean
	public static ApiBeanRegistration podApiRegistration() {
		return new ApiBeanRegistration(SymphonyApiConfig.SINGLE_BOT_INSTANCE_BEAN, SystemApi.class.getPackage().getName(), "getPodApi");
	}
	
	@Bean
	public static ApiBeanRegistration agentApiRegistration() {
		return new ApiBeanRegistration(SymphonyApiConfig.SINGLE_BOT_INSTANCE_BEAN, MessagesApi.class.getPackage().getName(), "getAgentApi");
	}
	
}
	
