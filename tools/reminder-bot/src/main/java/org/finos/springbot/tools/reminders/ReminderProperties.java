package org.finos.springbot.tools.reminders;

import java.time.ZoneId;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("springbot.reminder")
public class ReminderProperties {
	
	private String welcomeMessage = 
					  "Welcome!\n"
					+ "I am the Reminder Bot. If you mention a date or time in your chat message , I will suggest creating a reminder for it.\n"
					+ "type /help for help and /list to see existing reminders.";

	public String getWelcomeMessage() {
		return welcomeMessage;
	}

	public void setWelcomeMessage(String welcomeMessage) {
		this.welcomeMessage = welcomeMessage;
	}
	
	private ZoneId defaultTimeZone = ZoneId.of("Europe/London");

	public ZoneId getDefaultTimeZone() {
		return defaultTimeZone;
	}

	public void setDefaultTimeZone(ZoneId defaultTimeZone) {
		this.defaultTimeZone = defaultTimeZone;
	}

	private int defaultRemindBefore = 0;

	public int getDefaultRemindBefore() {
		return defaultRemindBefore;
	}

	public void setDefaultRemindBefore(int defaultRemindBefore) {
		this.defaultRemindBefore = defaultRemindBefore;
	}
}
