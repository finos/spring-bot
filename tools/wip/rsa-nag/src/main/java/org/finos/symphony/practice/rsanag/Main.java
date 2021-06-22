package org.finos.symphony.practice.rsanag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.MailSender;

@SpringBootApplication
@Configuration
public class Main {

	public static void main(String[] args) {
		SpringApplication.run(Main.class, args);
	}



}
