package org.finos.symphony.toolkit.tools.reminders;

import java.util.TimeZone;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("symphony.reminder")
public class ReminderProperties {
	
	private String welcomeMessage = "<messageML>"
					+ "<p>Welcome to <b>${entity.stream.roomName}</b></p><br />"
					+ "<p>I am the Reminder Bot. If you mention a date or time in your chat message , I will suggest creating a reminder for it.</p><br />"
					+ "<p>type /help for help and to see existing reminders</p>" + "</messageML>";

	public String getWelcomeMessage() {
		return welcomeMessage;
	}

	public void setWelcomeMessage(String welcomeMessage) {
		this.welcomeMessage = welcomeMessage;
	}
	
	private TimeZone defaultTimeZone = TimeZone.getTimeZone("Europe/London");

	public TimeZone getDefaultTimeZone() {
		return defaultTimeZone;
	}

	public void setDefaultTimeZone(TimeZone defaultTimeZone) {
		this.defaultTimeZone = defaultTimeZone;
	}

}
