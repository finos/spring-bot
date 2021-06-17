package org.finos.symphony.toolkit.workflow;

import java.util.List;

import org.finos.symphony.toolkit.json.EntityJson;
import org.finos.symphony.toolkit.workflow.annotations.ChatVariable;
import org.finos.symphony.toolkit.workflow.content.Message;
import org.finos.symphony.toolkit.workflow.content.Word;
import org.finos.symphony.toolkit.workflow.fixture.OurController;
import org.finos.symphony.toolkit.workflow.history.History;
import org.finos.symphony.toolkit.workflow.java.Exposed;
import org.finos.symphony.toolkit.workflow.java.mapping.ExposedHandlerMapping;
import org.finos.symphony.toolkit.workflow.java.mapping.HandlerExecutor;
import org.finos.symphony.toolkit.workflow.java.mapping.Mapping;
import org.finos.symphony.toolkit.workflow.java.resolvers.ResolverConfig;
import org.finos.symphony.toolkit.workflow.java.resolvers.WorkflowResolversFactory;
import org.finos.symphony.toolkit.workflow.sources.symphony.messages.SimpleMessageAction;
import org.finos.symphony.toolkit.workflow.sources.symphony.messages.SimpleMessageParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringExtension;


@SpringBootTest(classes = {
		TestHandlerMapping.TestConfig.class	,
		ResolverConfig.class
})
@ExtendWith(SpringExtension.class)
public class TestHandlerMapping {

	@Autowired
	OurController oc;
	
	@Autowired
	ExposedHandlerMapping hm;
	
	@MockBean
	History h;

	@Configuration
	static class TestConfig {
		
		
		@Bean
		public OurController ourController() {
			return new OurController();
		}
		
		@Bean
		public ExposedHandlerMapping handlerMapping(WorkflowResolversFactory wrf) {
			return new ExposedHandlerMapping(wrf);
		}
		
	}
	
	
	SimpleMessageParser smp = new SimpleMessageParser();
	
	@Test
	public void checkMappings() throws Exception {
		Assertions.assertEquals(11, hm.getHandlerMethods().size());
		getMappingsFor("list");
	}

	private List<Mapping<Exposed>> getMappingsFor(String s) throws Exception {
		EntityJson jsonObjects = new EntityJson();
		Message m = smp.parseNaked(s);
		Action a = new SimpleMessageAction(null, null, null, m, jsonObjects);
		return hm.getHandlers(a);
	}
	

	private List<HandlerExecutor> getExecutorsFor(String s) throws Exception {
		EntityJson jsonObjects = new EntityJson();
		Message m = smp.parseNaked(s);
		Action a = new SimpleMessageAction(null, null, null, m, jsonObjects);
		return hm.getExecutors(a);
	}
	
	@Test
	public void checkWildcardMapping() throws Exception {
		List<Mapping<Exposed>> mapped = getMappingsFor("ban zebedee");
		Assertions.assertTrue(mapped.size()  == 1);
	}
	
	@Test
	public void checkHandlerExecutors() throws Exception {
		List<HandlerExecutor> mapped = getExecutorsFor("ban zebedee");
		Assertions.assertTrue(mapped.size()  == 1);
		
		HandlerExecutor first = mapped.get(0);
		ChatVariable firstKey = first.getReplacements().keySet().iterator().next();
		
		Assertions.assertEquals("word", firstKey.value());
		Assertions.assertEquals(Word.of("zebedee"),first.getReplacements().get(firstKey));
	}
	
	@Test
	public void checkMethodCall() throws Exception {
		List<HandlerExecutor> mapped = getExecutorsFor("list");
		Assertions.assertTrue(mapped.size()  == 1);
		mapped.stream().forEach(e -> e.execute());
		
		Assertions.assertEquals("doCommand", oc.lastMethod);
	}
	

}
