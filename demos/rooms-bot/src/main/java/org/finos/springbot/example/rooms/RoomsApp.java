package org.finos.springbot.example.rooms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RoomsApp 
{
    public static void main( String[] args )
    {
    	SpringApplication.run(new Class[] {RoomsApp.class}, args);
    }
}
