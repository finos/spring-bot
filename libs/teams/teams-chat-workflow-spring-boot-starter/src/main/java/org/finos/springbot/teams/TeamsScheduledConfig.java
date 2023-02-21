package org.finos.springbot.teams;

import java.util.concurrent.TimeUnit;

import org.finos.springbot.teams.handlers.TeamsResponseHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;


@EnableScheduling
public class TeamsScheduledConfig {

	@Autowired
	private TeamsResponseHandler handler;
	
	@Scheduled(fixedDelay = 30, timeUnit = TimeUnit.SECONDS)
	//Task to run after a fixed delay. 
	//the duration between the end of last execution and the start of next execution is fixed
	public void scheduleRetryMessage() {
		handler.retryMessage();
	}
}
