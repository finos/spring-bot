package org.finos.springbot.examples.todo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ToDoWorkflowApp 
{
    public static void main( String[] args )
    {
    	SpringApplication.run(new Class[] {ToDoWorkflowApp.class}, args);
    }
}
