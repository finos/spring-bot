package org.finos.springbot.tool.poll;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PollBot 
{
    public static void main( String[] args )
    {
    	SpringApplication.run(new Class[] {PollBot.class}, args);
    }
    
    @Override
    @Bean
    public MessageActivityHandler overrideMessageActivityHandler() {
    	return SingleTennantMessageActivityHandler();
    }
}
