package org.finos.symphony.toolkit.stream.springit;

import org.finos.symphony.toolkit.stream.TestApplication;
import org.finos.symphony.toolkit.stream.fixture.NoddyCallback;
import org.finos.symphony.toolkit.stream.handler.SymphonyStreamHandlerFactory;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.symphony.api.agent.MessagesApi;
import com.symphony.api.id.SymphonyIdentity;


/**
 * Tests with web, but without cluster
 * 
 * @author Rob Moffat
 *
 */
@ExtendWith(SpringExtension.class)

@SpringBootTest(
	properties = { 
			"logging.level.org.finos.symphony.toolkit=debug"}, 
	classes={
			TestApplication.class
			}
	)
@ActiveProfiles("develop")
public class SpringComponentsWebNoClusterIT {
	
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
	public void testWithoutCluster() throws Exception {
		// post an event.
		api.v4StreamSidMessageCreatePost(null, someLocalConversation, "<messageML>This is a test</messageML>", null, null, null, null, null);

		// wait for it to arrive
		while (noddyCallback.getReceived().size() == 0) {
			Thread.sleep(50);
		}
		
		factory.stopAll();
	}
}