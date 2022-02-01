package org.finos.springbot.tool.reminders;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ReminderBotApp
{
    public static void main( String[] args )
    {
    	SpringApplication.run(new Class[] {ReminderBotApp.class}, args);
    }
}
