package org.finos.symphony.toolkit.stream;

import org.finos.springbot.entities.VersionSpaceHelp;
import org.finos.springbot.entityjson.ObjectMapperFactory;
import org.finos.springbot.entityjson.VersionSpace;
import org.finos.springbot.workflow.data.AbstractDataHandlerConfig;
import org.finos.springbot.workflow.data.EntityJsonConverter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootApplication
@EnableWebMvc
public class TestApplication {

	public static void main(String[] args) {
		SpringApplication.run(TestApplication.class, args);
	}

	@Bean
	public EntityJsonConverter ejc(ApplicationContext ac) {
		ObjectMapper om = ObjectMapperFactory
				.initialize(
					VersionSpaceHelp.extendedSymphonyVersionSpace(
						AbstractDataHandlerConfig.scanForWorkClasses(ac)));
		EntityJsonConverter ejc = new EntityJsonConverter(om);
		return ejc;
	}

}
