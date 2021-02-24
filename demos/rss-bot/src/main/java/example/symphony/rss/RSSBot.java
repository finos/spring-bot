package example.symphony.rss;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
public class RSSBot 
{
    public static void main( String[] args )
    {
    	SpringApplication.run(new Class[] {RSSBot.class}, args);
    }
}
