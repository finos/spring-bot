package org.finos.symphony.toolkit.workflow;

import org.finos.symphony.toolkit.workflow.fixture.OurController;
import org.finos.symphony.toolkit.workflow.java.mapping.ExposedHandlerMapping;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringExtension;


@SpringBootTest(classes = {
		TestHandlerMapping.TestConfig.class		
})
@ExtendWith(SpringExtension.class)
public class TestHandlerMapping {
	
	@Configuration
	static class TestConfig {
		
		
		@Bean
		public OurController ourController() {
			return new OurController();
		}
		
		@Bean
		public ExposedHandlerMapping handlerMapping() {
			return new ExposedHandlerMapping();
		}
		
	}
	
	@Autowired
	OurController oc;
	
	@Autowired
	ExposedHandlerMapping hm;
	
	@Test
	public void checkMappings() {
		Assertions.assertTrue(hm.getHandlerMethods().size() == 10);
	}

}
