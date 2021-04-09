package org.finos.symphony.webhookbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WebHookBot 
{
    public static void main( String[] args )
    {
    	SpringApplication.run(new Class[] {WebHookBot.class}, args);
    }
}
