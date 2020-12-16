package org.finos.symphony.toolkit.stream.springit;

import org.finos.symphony.toolkit.spring.api.SymphonyApiAutowireConfig;
import org.finos.symphony.toolkit.spring.api.SymphonyApiConfig;
import org.finos.symphony.toolkit.spring.api.builders.CXFApiBuilderConfig;
import org.finos.symphony.toolkit.spring.api.builders.JerseyApiBuilderConfig;
import org.finos.symphony.toolkit.stream.Participant;
import org.finos.symphony.toolkit.stream.app.NoddyCallback;
import org.finos.symphony.toolkit.stream.app.TestApplication;
import org.finos.symphony.toolkit.stream.cluster.ClusterMember;
import org.finos.symphony.toolkit.stream.cluster.ClusterMember.State;
import org.finos.symphony.toolkit.stream.filter.SymphonyLeaderEventFilter;
import org.finos.symphony.toolkit.stream.spring.SharedStreamObjectMapperConfig;
import org.finos.symphony.toolkit.stream.spring.SharedStreamSingleBotConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.symphony.api.agent.MessagesApi;
import com.symphony.api.id.SymphonyIdentity;


/**
 * Tests without spring web.
 * 
 * @author Rob Moffat
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(
	properties = { 
			"logging.level.org.finos.symphony.toolkit=debug",
			"symphony.stream.coordination-stream-id=y3EJYqKMwG7Jn7/YqyYdiX///pR3YrnTdA=="}, 
	classes={
			SharedStreamObjectMapperConfig.class,
			CXFApiBuilderConfig.class,
			SymphonyApiConfig.class,
			SymphonyApiAutowireConfig.class,
 			SharedStreamSingleBotConfig.class,
			NoddyCallback.class
			}
	)
@ActiveProfiles("develop")
public class SpringComponentsDummyIT {
	
	private String someLocalConversation = "Cscf+rSZRtGaOUrhkelBaH///o6ry5/5dA==";

	@MockBean
	TaskScheduler taskScheduler;
	
	@Autowired
	MessagesApi api;
	
	@Autowired
	SymphonyIdentity id;
	
	@Autowired
	Participant self;
	
	@Autowired
	NoddyCallback noddyCallback;
	
	@Autowired
	ClusterMember cm;
	
	@Autowired
	SymphonyLeaderEventFilter eventFilter;

	
	@Test
	public void testDummyCluster() throws Exception {
		// wait for this cluster to become leader
		while (cm.getState() != State.LEADER) {
			Thread.sleep(50);
		}

		// wait for the event to say it's leader.
		while (!eventFilter.isActive()) {
			Thread.sleep(50);
		}
		
		// post an event.
		api.v4StreamSidMessageCreatePost(null, someLocalConversation, "<messageML>This is a test</messageML>", null, null, null, null, null);

		// wait for it to arrive
		while (noddyCallback.getReceived().size() == 0) {
			Thread.sleep(50);
		}
	}
}