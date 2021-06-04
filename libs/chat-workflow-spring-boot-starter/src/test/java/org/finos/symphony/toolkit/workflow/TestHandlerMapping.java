package org.finos.symphony.toolkit.workflow;

import java.util.List;

import org.finos.symphony.toolkit.json.EntityJson;
import org.finos.symphony.toolkit.workflow.content.Message;
import org.finos.symphony.toolkit.workflow.fixture.OurController;
import org.finos.symphony.toolkit.workflow.java.mapping.ExposedHandlerMapping;
import org.finos.symphony.toolkit.workflow.java.mapping.HandlerExecutor;
import org.finos.symphony.toolkit.workflow.sources.symphony.messages.SimpleMessageAction;
import org.finos.symphony.toolkit.workflow.sources.symphony.messages.SimpleMessageParser;
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
	
	SimpleMessageParser smp = new SimpleMessageParser();
	
	@Test
	public void checkMappings() throws Exception {
		Assertions.assertTrue(hm.getHandlerMethods().size() == 10);
		
		getMappingsFor("new claim");
		
		
	}

	private List<HandlerExecutor> getMappingsFor(String s) throws Exception {
		EntityJson jsonObjects = new EntityJson();
		Message m = smp.parse(s, jsonObjects);
		Action a = new SimpleMessageAction(null, null, null, m, jsonObjects);
		return hm.getHandlers(a);
	}

}
