package example.symphony.demoworkflow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
public class ToDoWorkflowApp 
{
    public static void main( String[] args )
    {
    	SpringApplication.run(new Class[] {ToDoWorkflowApp.class}, args);
    }
}
