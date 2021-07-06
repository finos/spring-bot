package org.finos.symphony.toolkit.tools.reminders;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAutoConfiguration
@EnableScheduling
public class ReminderBotApp
{
    public static void main( String[] args )
    {
    	SpringApplication.run(new Class[] {ReminderBotApp.class}, args);
    }
}
