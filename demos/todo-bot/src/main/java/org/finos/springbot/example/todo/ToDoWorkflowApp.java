package org.finos.springbot.example.todo;

import org.finos.springbot.workflow.response.handlers.ResponseHandlers;
import org.finos.springbot.workflow.welcome.RoomWelcomeEventConsumer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ToDoWorkflowApp 
{
    public static void main( String[] args )
    {
    	SpringApplication.run(new Class[] {ToDoWorkflowApp.class}, args);
    }
    
    @Bean
    public RoomWelcomeEventConsumer rwec(ResponseHandlers rh) {
    	return new RoomWelcomeEventConsumer(rh);
    }
}
