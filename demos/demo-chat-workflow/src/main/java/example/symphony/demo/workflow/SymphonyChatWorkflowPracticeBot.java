package example.symphony.demo.workflow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"example.symphony.demo","com.github.deutschebank.symphony"})
public class SymphonyChatWorkflowPracticeBot 
{
    public static void main( String[] args )
    {
    	SpringApplication.run(new Class[] {SymphonyChatWorkflowPracticeBot.class}, args);
    }
}
