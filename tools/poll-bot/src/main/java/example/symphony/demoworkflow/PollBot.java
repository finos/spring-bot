package example.symphony.demoworkflow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PollBot 
{
    public static void main( String[] args )
    {
    	SpringApplication.run(new Class[] {PollBot.class}, args);
    }
}
