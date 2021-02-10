package example.symphony.demoapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication
@EnableWebMvc
public class DemoAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoAppApplication.class, args);
	}

}
