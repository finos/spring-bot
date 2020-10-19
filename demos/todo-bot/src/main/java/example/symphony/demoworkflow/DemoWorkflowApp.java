package example.symphony.demoworkflow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"example.symphony.demoworkflow","com.github.deutschebank.symphony"})
public class DemoWorkflowApp 
{
    public static void main( String[] args )
    {
    	SpringApplication.run(new Class[] {DemoWorkflowApp.class}, args);
    }
}
