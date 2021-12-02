package org.finos.symphony.toolkit.stream.springit;

import org.finos.springbot.workflow.data.DataHandlerConfig;
import org.finos.symphony.toolkit.spring.api.SymphonyApiAutowireConfig;
import org.finos.symphony.toolkit.spring.api.SymphonyApiConfig;
import org.finos.symphony.toolkit.spring.api.builders.CXFApiBuilderConfig;
import org.finos.symphony.toolkit.stream.fixture.NoddyCallback;
import org.finos.symphony.toolkit.stream.handler.SharedStreamHandlerConfig;
import org.finos.symphony.toolkit.stream.handler.SymphonyStreamHandlerFactory;
import org.finos.symphony.toolkit.stream.single.SharedStreamSingleBotConfig;
import org.finos.symphony.toolkit.stream.springit.SpringComponentsNoClusterIT.TestContext;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.symphony.api.agent.MessagesApi;
import com.symphony.api.id.SymphonyIdentity;


/**
 * Tests without spring web (and therefore, no cluster)
 * 
 * @author Rob Moffat
 *
 */
@ExtendWith(SpringExtension.class)

@SpringBootTest(
	properties = { 
			"logging.level.org.finos.symphony.toolkit=debug"}, 
	classes={
			TestContext.class,
			NoddyCallback.class,
			CXFApiBuilderConfig.class,
			SymphonyApiConfig.class,
			SymphonyApiAutowireConfig.class,
			SharedStreamHandlerConfig.class,
 			SharedStreamSingleBotConfig.class,
 			DataHandlerConfig.class
		}
	)
@ActiveProfiles("develop")
public class SpringComponentsNoClusterIT {
	
	static class TestContext {
	
		/**
		 * Needed since we don't load spring-web.
		 * @return
		 */
		@Bean 
		public ObjectMapper objectMapper() {
			
			return new ObjectMapper();
		}
		
	}
	
	
	private String someLocalConversation = "Cscf+rSZRtGaOUrhkelBaH///o6ry5/5dA==";

	@MockBean
	TaskScheduler taskScheduler;
	
	@Autowired
	MessagesApi api;
	
	@Autowired
	SymphonyIdentity id;
	
	@Autowired
	NoddyCallback noddyCallback;
	
	@Autowired
	SymphonyStreamHandlerFactory factory;
		
	@Test
	public void testDummyCluster() throws Exception {

		// post an event.
		api.v4StreamSidMessageCreatePost(null, someLocalConversation, "<messageML>This is a test</messageML>", null, null, null, null, null);

		// wait for it to arrive
		while (noddyCallback.getReceived().size() == 0) {
			Thread.sleep(50);
		}
		
		factory.stopAll();
	}
}