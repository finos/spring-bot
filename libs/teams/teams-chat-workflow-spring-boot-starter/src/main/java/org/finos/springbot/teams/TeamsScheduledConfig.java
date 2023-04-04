package org.finos.springbot.teams;

import org.finos.springbot.teams.handlers.ActivityHandler;
import org.finos.springbot.teams.handlers.SimpleActivityHandler;
import org.finos.springbot.teams.handlers.retry.AbstractRetryingActivityHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

@EnableScheduling
public class TeamsScheduledConfig implements SchedulingConfigurer {

	private static final Logger LOG = LoggerFactory.getLogger(TeamsScheduledConfig.class);

	@Autowired
	private ActivityHandler retryHandler;

	@Value("${teams.retry.time:30000}")
	private long teamsRetrySchedulerCron;

	@Override
	public void configureTasks(ScheduledTaskRegistrar scheduledTaskRegistrar) {
		if (retryHandler instanceof SimpleActivityHandler) {
			LOG.info("No-operation retry handler is configured.");
		} else {
			Runnable runnable = () -> scheduleRetryMessage();
			scheduledTaskRegistrar.addFixedDelayTask(runnable, teamsRetrySchedulerCron);
		}
	}

	private void scheduleRetryMessage() {

		((AbstractRetryingActivityHandler) retryHandler).retryMessage();
	}
}
