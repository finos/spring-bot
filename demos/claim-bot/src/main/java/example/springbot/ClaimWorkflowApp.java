package example.springbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ClaimWorkflowApp 
{
    public static void main( String[] args )
    {
    	SpringApplication.run(new Class[] {ClaimWorkflowApp.class}, args);
    }
}
