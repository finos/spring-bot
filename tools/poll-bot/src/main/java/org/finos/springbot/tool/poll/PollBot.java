package org.finos.springbot.tool.poll;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;

@SpringBootApplication
public class PollBot 
{
    public static void main( String[] args )
    {
    	SpringApplication.run(new Class[] {PollBot.class}, args);
    }
    
    @Bean
    public TaskScheduler taskScheduler() {
    	return new ConcurrentTaskScheduler();
    }
}
