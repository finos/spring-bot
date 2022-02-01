package org.finos.springbot.tool.rssbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class RSSBot 
{
    public static void main( String[] args )
    {
    	SpringApplication.run(new Class[] {RSSBot.class}, args);
    }
}
